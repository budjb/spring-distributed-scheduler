package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.RunningState
import com.budjb.spring.distributed.scheduler.workload.AbstractWorkloadRunnable
import com.budjb.spring.distributed.scheduler.workload.Workload

class TestRunnable extends AbstractWorkloadRunnable {
    /**
     * Constructor.
     *
     * @param workload Workload that this runnable services.
     */
    TestRunnable(Workload workload) {
        super(workload)
    }

    @Override
    void run() {
        setRunningState(RunningState.RUNNING)

        while (!isInterrupted()) {
            sleep(250)
        }

        if (!getRunningState().isTerminated()) {
            setRunningState(RunningState.STOPPED)
        }
    }
}
