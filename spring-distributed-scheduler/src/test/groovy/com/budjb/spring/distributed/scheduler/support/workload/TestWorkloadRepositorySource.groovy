package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.workload.Workload
import com.budjb.spring.distributed.scheduler.workload.WorkloadRepositorySource

class TestWorkloadRepositorySource implements WorkloadRepositorySource {
    private final Set<TestWorkload> registeredWorkloads

    TestWorkloadRepositorySource(Set<TestWorkload> registeredWorkloads) {
        this.registeredWorkloads = registeredWorkloads
    }

    void addWorkload(TestWorkload workload) {
        registeredWorkloads.add(workload)
    }

    void removeWorkload(TestWorkload workload) {
        registeredWorkloads.remove(workload)
    }

    @Override
    Set<Workload> queryWorkloads() {
        return registeredWorkloads
    }
}
