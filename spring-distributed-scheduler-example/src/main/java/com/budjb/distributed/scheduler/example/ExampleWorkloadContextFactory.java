package com.budjb.distributed.scheduler.example;

import com.budjb.spring.distributed.scheduler.workload.SingleThreadedWorkloadContext;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadContext;
import com.budjb.spring.distributed.scheduler.workload.WorkloadContextFactory;
import org.springframework.stereotype.Component;

@Component
public class ExampleWorkloadContextFactory implements WorkloadContextFactory {
    @Override
    public boolean supports(Workload workload) {
        return workload instanceof ExampleWorkload;
    }

    @Override
    public WorkloadContext createContext(Workload workload) {
        return new SingleThreadedWorkloadContext(new ExampleWorkloadRunnable(workload));
    }
}
