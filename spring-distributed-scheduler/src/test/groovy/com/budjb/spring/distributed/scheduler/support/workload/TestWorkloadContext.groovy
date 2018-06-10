package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.workload.SingleThreadedWorkloadContext
import com.budjb.spring.distributed.scheduler.workload.Workload
import com.budjb.spring.distributed.scheduler.workload.WorkloadRunnable

class TestWorkloadContext extends SingleThreadedWorkloadContext {
    /**
     * Constructor.
     *
     * @param workload Workload.
     */
    TestWorkloadContext(Workload workload) {
        super(workload)
    }

    @Override
    protected WorkloadRunnable createRunnable() {
        return new TestRunnable(workload)
    }
}
