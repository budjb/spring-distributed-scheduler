package com.budjb.spring.distributed.scheduler;

import com.budjb.spring.distributed.lock.DistributedLock;
import com.budjb.spring.distributed.lock.DistributedLockProvider;
import com.budjb.spring.distributed.scheduler.cluster.ClusterManager;
import com.budjb.spring.distributed.scheduler.cluster.ClusterMember;
import com.budjb.spring.distributed.scheduler.instruction.ReportInstruction;
import com.budjb.spring.distributed.scheduler.instruction.SchedulerInstruction;
import com.budjb.spring.distributed.scheduler.instruction.ShutdownInstruction;
import com.budjb.spring.distributed.scheduler.strategy.SchedulerStrategy;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;
import com.budjb.spring.distributed.scheduler.workload.WorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The distributed scheduler is responsible for managing load balance scheduling and
 * instruction submission to cluster members. The actual load balancing logic is provided
 * by a {@link SchedulerStrategy} implementation.
 */
public class DistributedScheduler implements DisposableBean {
    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(DistributedScheduler.class);

    /**
     * Distributed lock provider.
     */
    private final DistributedLockProvider distributedLockProvider;

    /**
     * Scheduler configuration properties.
     */
    private final SchedulerProperties schedulerProperties;

    /**
     * Cluster manager.
     */
    private final ClusterManager<ClusterMember> clusterManager;

    /**
     * Workload scheduler strategy.
     */
    private final SchedulerStrategy schedulerStrategy;

    /**
     * Workload repository.
     */
    private final WorkloadRepository workloadRepository;

    /**
     * Constructor.
     *
     * @param distributedLockProvider Distributed lock provider.
     * @param clusterManager          Cluster manager.
     * @param schedulerProperties     Scheduler configuration properties.
     * @param schedulerStrategy       Strategy implementation used by the scheduler to load balancer the cluster.
     * @param workloadRepository      Workload repository.
     */
    public DistributedScheduler(
        DistributedLockProvider distributedLockProvider,
        ClusterManager<ClusterMember> clusterManager,
        SchedulerProperties schedulerProperties,
        SchedulerStrategy schedulerStrategy,
        WorkloadRepository workloadRepository
    ) {
        this.distributedLockProvider = distributedLockProvider;
        this.clusterManager = clusterManager;
        this.schedulerProperties = schedulerProperties;
        this.schedulerStrategy = schedulerStrategy;
        this.workloadRepository = workloadRepository;
    }

    /**
     * Attempts to perform a forceful re-balance when the application starts up.
     * This will not occur if another node is actively re-balancing.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStart() {
        schedule(true);
    }

    /**
     * Attempt to conduct a non-forceful re-balance every 10 seconds. In actuality,
     * re-balancing will only occur every 5 minutes, but the check is done often so
     * that a more sophisticated cluster coordination system is not needed.
     */
    @Scheduled(fixedRateString = "${indexer.cluster.rebalance-poll-interval:30000}", initialDelayString = "${indexer.cluster.rebalance-poll-delay:30000}")
    public void schedule() {
        schedule(false);
    }

    /**
     * Re-balances the collector cluster so that load is even.
     * <p>
     * The logic is a bit of a doozy, so here's the general outline of the algorithm:
     * 1. Get the complete list of registered endpoints (those endpoints we *should* be indexing).
     * 2. Remove any collectors for endpoints that no longer exist.
     * 3. Determine which endpoints we currently are indexing (even if they're in an error state).
     * 4. Assign each endpoint that's not currently being indexed to the collector node with the
     * least load (load being the number of endpoints being indexed by the node).
     * 5. Balance out the workload so that size of the load on each node is within 1. This requires
     * a bit of loop iteration.
     * 6. Restart any remaining collectors that are in an error state.
     * 7. Submit *only* remove-type instructions to each node first. We must ensure that any
     * collectors that need to be stopped have completed before starting them on other nodes
     * to avoid situations where multiple nodes are collecting the same endpoints.
     * 8. Submit add or restart type instructions to each node.
     *
     * @param force If true, will disregard the time check and re-balance now.
     */
    public void schedule(boolean force) {
        DistributedLock lock = distributedLockProvider.getDistributedLock("distributed-scheduler-lock" /* TODO: lock name configurable? */);

        // TODO: configurable?
        try {
            if (lock.supportsLeases()) {
                if (!lock.tryLock(0, TimeUnit.MILLISECONDS, 10, TimeUnit.MINUTES)) {
                    return;
                }
            }
            else {
                if (!lock.tryLock(0, TimeUnit.MILLISECONDS)) {
                    return;
                }
            }

            if (!force && !isTimeToSchedule()) {
                lock.unlock();
                return;
            }
        }
        catch (InterruptedException ignored) {
            if (!lock.supportsLeases() || (lock.supportsLeases() && lock.isLocked())) {
                lock.unlock();
            }
            return;
        }

        try {
            Set<Workload> registeredWorkloads = workloadRepository.getWorkloads();

            Map<ClusterMember, WorkloadReport> reports = clusterManager.submitInstruction(new ReportInstruction());
            if (reports.size() == 0) {
                throw new IllegalStateException("Received no workload reports from any cluster member nodes");
            }

            List<Map<ClusterMember, SchedulerInstruction>> instructions = schedulerStrategy.schedule(registeredWorkloads, reports);

            for (Map<ClusterMember, SchedulerInstruction> instructionSet : instructions) {
                clusterManager.submitInstructions(instructionSet);
            }

            clusterManager.setScheduleTime(System.currentTimeMillis() + schedulerProperties.getRebalanceInterval());
        }
        catch (Exception e) {
            log.error("Unexpected exception encountered while scheduling workloads", e);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Shuts down all workloads.
     */
    public void shutdown() {
        try {
            clusterManager.submitInstruction(new ShutdownInstruction());
        }
        catch (Exception e) {
            log.error("unable to shut down workloads", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        shutdown();
    }

    /**
     * Determines if it's time to schedule workloads.
     *
     * @return whether it's time to schedule workloads.
     */
    private boolean isTimeToSchedule() {
        long time = clusterManager.getScheduleTime();
        return time <= 0 || System.currentTimeMillis() >= time;
    }
}
