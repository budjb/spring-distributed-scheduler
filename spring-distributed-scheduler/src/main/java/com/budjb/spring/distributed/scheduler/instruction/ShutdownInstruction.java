package com.budjb.spring.distributed.scheduler.instruction;

import com.budjb.spring.distributed.scheduler.workload.WorkloadProviderRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * Instructs a cluster to stop all running workloads.
 */
public class ShutdownInstruction implements Instruction<Void> {
    @Autowired
    transient WorkloadProviderRegistry workloadProviderRegistry;

    @Override
    public Void call() throws Exception {
        Set<Future<?>> futures = workloadProviderRegistry.shutdown();

        while (futures.size() > 0) {
            futures.removeIf(Future::isDone);
            Thread.sleep(250);
        }

        return null;
    }
}
