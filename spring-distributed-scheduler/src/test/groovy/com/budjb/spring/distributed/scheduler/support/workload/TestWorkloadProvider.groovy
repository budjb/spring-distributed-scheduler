package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.workload.AbstractWorkloadProvider
import com.budjb.spring.distributed.scheduler.workload.Workload
import com.budjb.spring.distributed.scheduler.workload.WorkloadContext

class TestWorkloadProvider extends AbstractWorkloadProvider {
    private final Set<TestWorkload> registeredWorkloads

    TestWorkloadProvider(Set<TestWorkload> registeredWorkloads) {
        this.registeredWorkloads = registeredWorkloads
    }

    @Override
    protected WorkloadContext createContext(Workload workload) {
        return new TestWorkloadContext(workload)
    }

    @Override
    Class<TestWorkload> getSupportedWorkloadType() {
        return TestWorkload.class
    }

    @Override
    Set<TestWorkload> queryRegisteredWorkloads() {
        return registeredWorkloads
    }

    void addWorkload(TestWorkload workload) {
        registeredWorkloads.add(workload)
    }

    void removeWorkload(TestWorkload workload) {
        registeredWorkloads.remove(workload)
    }
}
