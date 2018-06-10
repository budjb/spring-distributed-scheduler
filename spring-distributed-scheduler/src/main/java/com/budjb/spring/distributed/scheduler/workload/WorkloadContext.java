package com.budjb.spring.distributed.scheduler.workload;

import com.budjb.spring.distributed.scheduler.RunningState;

/**
 * Manages the runtime of a specific workload.
 */
public interface WorkloadContext {
    /**
     * Returns the workload context acts on.
     *
     * @return the workload context acts on.
     */
    Workload getWorkload();

    /**
     * Generates a workload report entry for the workload being processed by this context.
     *
     * @return a workload report entry.
     */
    WorkloadReport.Entry getWorkloadReportEntry();

    /**
     * Starts the workload.
     */
    void start();

    /**
     * Signals that the workload should stop and gracefully terminate. This call is non-blocking
     * and does not wait for the workload to stop. Running status can subsequently be determined
     * through the use of {@link #getRunningState} and {@link #isStopped}.
     */
    void stop();

    /**
     * Returns whether the workload has stopped. A runnable is considered stopped when
     * its thread has died or its running state is terminated.
     *
     * @return whether the runnable has stopped.
     */
    boolean isStopped();

    /**
     * Stops execution of the workload's thread. This should only be used as
     * a last-ditch attempt to get the workload to stop collecting.
     */
    void terminate();

    /**
     * Simulate a workload failure. This is useful for testing.
     */
    void simulateFailure();

    /**
     * Returns the running state of the workload.
     *
     * @return the running state of the workload.
     */
    RunningState getRunningState();
}