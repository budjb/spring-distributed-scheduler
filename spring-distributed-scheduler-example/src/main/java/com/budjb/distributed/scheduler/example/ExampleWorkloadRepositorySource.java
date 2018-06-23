package com.budjb.distributed.scheduler.example;

import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadRepositorySource;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ExampleWorkloadRepositorySource implements WorkloadRepositorySource {
    private static final Set<Workload> workloads;

    static {
        workloads = new HashSet<>();
        workloads.add(new ExampleWorkload("a"));
        workloads.add(new ExampleWorkload("b"));
        workloads.add(new ExampleWorkload("c"));
    }

    @Override
    public Set<Workload> queryWorkloads() {
        return workloads;
    }
}
