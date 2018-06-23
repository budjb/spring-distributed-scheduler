package com.budjb.spring.distributed.scheduler.workload;

import com.budjb.spring.distributed.scheduler.RunningState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base implementation of {@link WorkloadContext} that provides a framework around
 * running a workload that will only ever use one {@link WorkloadRunnable} and thread.
 */
public abstract class SingleThreadedWorkloadContext implements WorkloadContext {
    /**
     * Workload.
     */
    private final Workload workload;
    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SingleThreadedWorkloadContext.class);
    /**
     * Workload runnable.
     */
    private WorkloadRunnable runnable;

    /**
     * Workload runnable thread.
     */
    private Thread thread;

    /**
     * Constructor.
     *
     * @param workload Workload.
     */
    protected SingleThreadedWorkloadContext(Workload workload) {
        this.workload = workload;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Workload getWorkload() {
        return workload;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadReport.Entry getWorkloadReportEntry() {
        return new WorkloadReport.Entry(workload, runnable.getRunningState(), runnable.getException() != null ? runnable.getException().getMessage() : null);
    }

    /**
     * Creates the runnable instance for the workload.
     *
     * @return New runnable instance for the workload.
     */
    protected abstract WorkloadRunnable createRunnable();

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (thread != null) {
            if (thread.isAlive()) {
                throw new IllegalStateException("execution thread for workload " + workload.toString() + " has already been started");
            }
            else {
                thread = null;
            }
        }

        runnable = createRunnable();
        thread = new Thread(runnable, "runnable-" + workload.getUrn());
        thread.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (thread != null && thread.isAlive() && !getRunningState().isTerminal()) {
            runnable.interrupt();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStopped() {
        if (thread == null) {
            return true;
        }

        if (!thread.isAlive()) {
            thread = null;

            if (!runnable.getRunningState().isTerminated()) {
                runnable.terminate();
            }

            return true;
        }

        return runnable.getRunningState().isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate() {
        if (thread == null) {
            return;
        }

        try {
            thread.interrupt();
            thread.join();
        }
        catch (InterruptedException e) {
            log.error("unable to successfully terminate a workload thread due to being interrupted", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fail() {
        runnable.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RunningState getRunningState() {
        return runnable.getRunningState();
    }
}
