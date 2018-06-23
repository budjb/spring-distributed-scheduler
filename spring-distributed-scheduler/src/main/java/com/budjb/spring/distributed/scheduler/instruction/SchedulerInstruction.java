package com.budjb.spring.distributed.scheduler.instruction;

import com.budjb.spring.distributed.scheduler.strategy.SchedulerAction;
import com.budjb.spring.distributed.scheduler.workload.WorkloadContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An instruction created by the scheduler system that conducts changes to workload
 * assignments for a specific cluster member.
 */
public class SchedulerInstruction implements Instruction<Boolean> {
    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SchedulerInstruction.class);

    /**
     * Scheduler actions to perform.
     */
    private final List<SchedulerAction> actions;
    /**
     * Workload context manager.
     */
    private transient WorkloadContextManager workloadContextManager;

    /**
     * Constructor.
     *
     * @param actions the scheduler actions to perform.
     */
    public SchedulerInstruction(List<SchedulerAction> actions) {
        this.actions = actions;
    }

    /**
     * Sets the workload context manager.
     *
     * @param workloadContextManager Workload context manager.
     */
    @Autowired
    public void setWorkloadContextManager(WorkloadContextManager workloadContextManager) {
        this.workloadContextManager = workloadContextManager;
    }

    /**
     * Returns the scheduler actions to perform.
     *
     * @return the scheduler actions to perform.
     */
    public List<SchedulerAction> getActions() {
        return actions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean call() throws InterruptedException {
        List<Future<?>> futures = new ArrayList<>();

        for (SchedulerAction action : actions) {
            try {
                switch (action.getActionType()) {
                    case ADD:
                        workloadContextManager.start(action.getWorkload());
                        break;

                    case REMOVE:
                        futures.add(workloadContextManager.stop(action.getWorkload()));
                        break;

                    case RESTART:
                        futures.add(workloadContextManager.restart(action.getWorkload()));
                        break;

                    case FAIL:
                        workloadContextManager.fail(action.getWorkload());
                        break;

                    default:
                        throw new UnsupportedOperationException("action type " + action.getActionType().toString() + " is unsupported");
                }
            }
            catch (UnsupportedOperationException e) {
                throw e;
            }
            catch (Exception e) {
                log.error("Unhandled exception encountered while starting workload " + action.getWorkload().toString(), e);
            }
        }

        while (futures.size() > 0) {
            futures.removeIf(Future::isDone);
            Thread.sleep(250);
        }

        return true;
    }
}
