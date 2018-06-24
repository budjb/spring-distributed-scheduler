package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.workload.SingleThreadedWorkloadContext
import com.budjb.spring.distributed.scheduler.workload.Workload
import com.budjb.spring.distributed.scheduler.workload.WorkloadContext
import com.budjb.spring.distributed.scheduler.workload.WorkloadContextFactory

class TestWorkloadContextFactory implements WorkloadContextFactory {
    @Override
    WorkloadContext createContext(Workload workload) {
        return new SingleThreadedWorkloadContext(new TestRunnable(workload))
    }

    @Override
    boolean supports(Workload workload) {
        return workload instanceof TestWorkload
    }
}
