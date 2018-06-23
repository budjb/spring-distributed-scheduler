package com.budjb.distributed.scheduler.example;

import com.budjb.spring.distributed.scheduler.workload.SingleThreadedWorkloadContext;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadRunnable;

public class ExampleWorkloadContext extends SingleThreadedWorkloadContext {
    /**
     * Constructor.
     *
     * @param workload Workload.
     */
    public ExampleWorkloadContext(Workload workload) {
        super(workload);
    }

    @Override
    protected WorkloadRunnable createRunnable() {
        return new ExampleWorkloadRunnable(getWorkload());
    }
}
