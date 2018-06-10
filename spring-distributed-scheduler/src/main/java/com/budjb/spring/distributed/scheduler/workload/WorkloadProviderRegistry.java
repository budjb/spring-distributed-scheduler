package com.budjb.spring.distributed.scheduler.workload;

import com.budjb.spring.distributed.scheduler.SchedulerProperties;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Aggregates all workload providers to provide a single interface to manage workloads,
 * regardless of the workload type.
 */
public class WorkloadProviderRegistry {
    /**
     * Executors service.
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Workload providers.
     */
    private final Map<Class<? extends Workload>, WorkloadProvider> workloadProviders = new HashMap<>();

    /**
     * Scheduler properties.
     */
    private final SchedulerProperties schedulerProperties;

    /**
     * Constructor.
     *
     * @param workloadProviders   the set of available workload providers.
     * @param schedulerProperties scheduler properties.
     */
    public WorkloadProviderRegistry(List<WorkloadProvider> workloadProviders, SchedulerProperties schedulerProperties) {
        workloadProviders.forEach(p -> {
            if (this.workloadProviders.containsKey(p.getSupportedWorkloadType())) {
                throw new IllegalArgumentException("multiple workload providers that support the same workload type were found");
            }
            this.workloadProviders.put(p.getSupportedWorkloadType(), p);
        });

        this.schedulerProperties = schedulerProperties;
    }

    /**
     * Returns the set of registered workload providers.
     *
     * @return the set of registered workload providers.
     */
    public Collection<WorkloadProvider> getWorkloadProviders() {
        return workloadProviders.values();
    }

    /**
     * Returns a set of workloads aggregated from all available workload providers.
     *
     * @return a set of workloads aggregated from all available workload providers.
     */
    public Set<Workload> getWorkloads() {
        Set<Workload> workloads = new HashSet<>();

        for (WorkloadProvider provider : workloadProviders.values()) {
            workloads.addAll(provider.queryRegisteredWorkloads());
        }

        return workloads;
    }

    /**
     * Creates a workload report containing entries for all providers and workloads.
     *
     * @return a complete workload report.
     */
    public WorkloadReport getWorkloadReport() {
        return new WorkloadReport(workloadProviders.values().stream().flatMap(p -> p.getContexts().stream()).map(WorkloadContext::getWorkloadReportEntry).collect(Collectors.toList()));
    }

    /**
     * Returns the workload provider for the given workload.
     *
     * @param workload Workload to get a provider for.
     * @param <W>      Workload type.
     * @return the workload provider for the given workload.
     */
    private <W extends Workload> WorkloadProvider getWorkloadProvider(W workload) {
        if (!workloadProviders.containsKey(workload.getClass())) {
            throw new IllegalArgumentException("no workload provider found that supports workload type " + workload.getClass().getName());
        }
        return workloadProviders.get(workload.getClass());
    }

    /**
     * Starts the given workload.
     *
     * @param workload Workload to get a provider for.
     */
    public void start(Workload workload) {
        getWorkloadProvider(workload).registerWorkload(workload).start();
    }

    /**
     * Stops the given workload.
     *
     * @param workload Workload to get a provider for.
     * @return A future for the process of stopping the workload.
     */
    public Future<?> stop(Workload workload) {
        return stop(getWorkloadProvider(workload).removeContext(workload));
    }

    /**
     * Stops the given workload context.
     *
     * @param context workload context to stop.
     * @return A future for the process of stopping the workload context.
     */
    private Future<?> stop(WorkloadContext context) {
        return executorService.submit(() -> {
            context.stop();

            long end = System.currentTimeMillis() + schedulerProperties.getInstruction().getPollTimeout();

            while (System.currentTimeMillis() < end) {
                if (context.isStopped()) {
                    return;
                }
                try {
                    Thread.sleep(schedulerProperties.getInstruction().getPollInterval());
                }
                catch (InterruptedException ignored) {
                    // ignored
                }
            }

            if (!context.isStopped()) {
                context.terminate();
            }
        });
    }

    /**
     * Restarts the given workload.
     *
     * @param workload Workload to restart.
     * @return A future for the process of restarting the workload.
     */
    public Future<?> restart(Workload workload) {
        WorkloadProvider provider = getWorkloadProvider(workload);
        WorkloadContext context = provider.removeContext(workload);

        return executorService.submit(() -> {
            Future<?> future = stop(context);

            while (!future.isDone()) {
                try {
                    future.get();
                }
                catch (InterruptedException ignored) {
                    // ignored
                }
                catch (ExecutionException e) {
                    // TODO: log it!
                    return;
                }
            }

            provider.registerWorkload(workload).start();
        });
    }

    /**
     * Interrupts and forces an error state on the given workload.
     *
     * @param workload Workload to simulate failure for.
     */
    public void simulateFailure(Workload workload) {
        getWorkloadProvider(workload).getContext(workload).simulateFailure();
    }

    /**
     * Shuts down all workloads.
     *
     * @return a future to track its execution state.
     */
    public Set<Future<?>> shutdown() {
        return getWorkloadProviders().stream().flatMap(p -> p.removeAll().stream()).map(this::stop).collect(Collectors.toSet());
    }
}
