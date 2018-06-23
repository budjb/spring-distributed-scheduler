package com.budjb.spring.distributed.scheduler.instruction;

import com.budjb.spring.distributed.scheduler.workload.WorkloadContextManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Future;

/**
 * Instructs a cluster to stop all running workloads.
 */
public class ShutdownInstruction implements Instruction<Void> {
    @Autowired
    transient WorkloadContextManager workloadContextManager;

    @Override
    public Void call() throws Exception {
        Future future = workloadContextManager.shutdown();
        future.get();
        return null;
    }
}
