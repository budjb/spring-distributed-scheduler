package com.budjb.spring.distributed.scheduler.strategy;

import com.budjb.spring.distributed.scheduler.workload.Workload;

import java.io.Serializable;

/**
 * Contains an individual action to perform on a specific workload.
 */
public class SchedulerAction implements Serializable {
    /**
     * The workload to act on.
     */
    private final Workload workload;

    /**
     * Action to perform.
     */
    private final ActionType actionType;

    /**
     * Constructor.
     *
     * @param workload   workload the action should be taken on.
     * @param actionType action to take.
     */
    public SchedulerAction(Workload workload, ActionType actionType) {
        this.workload = workload;
        this.actionType = actionType;
    }

    /**
     * Returns the workload to perform the action on.
     *
     * @return the workload to perform the action on.
     */
    public Workload getWorkload() {
        return workload;
    }

    /**
     * Returns the action to perform.
     *
     * @return the action to perform.
     */
    public ActionType getActionType() {
        return actionType;
    }
}
