package com.budjb.spring.distributed.scheduler;

/**
 * Represents the various states that the processing of a workload can take.
 */
public enum RunningState {
    NOT_STARTED(false, false),
    STARTING(false, false),
    RUNNING(false, false),
    STOPPING(true, false),
    STOPPED(true, true),
    ERROR(true, true);

    /**
     * Whether the running state is considered terminal; i.e., the workload is not yet
     * stopped but is in the processing of stopping.
     */
    private final boolean terminal;

    /**
     * Whether the running state represents a state where the workload has finished
     * its processing, whether by success or failure.
     */
    private final boolean terminated;

    /**
     * Constructor.
     *
     * @param terminal Whether the state is terminal.
     * @param terminated Whether the state represents a workload that has terminated.
     */
    RunningState(boolean terminal, boolean terminated) {
        this.terminal = terminal;
        this.terminated = terminated;
    }

    /**
     * Returns whether the state is terminal.
     *
     * @return Whether the state is terminal.
     */
    public boolean isTerminal() {
        return terminal;
    }

    /**
     * Returns whether the state represents a workload that has terminated.
     * @return Whether the state represents a workload that has terminated.
     */
    public boolean isTerminated() {
        return terminated;
    }
}
