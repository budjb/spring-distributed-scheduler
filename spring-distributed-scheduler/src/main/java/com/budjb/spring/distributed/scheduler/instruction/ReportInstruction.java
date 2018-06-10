package com.budjb.spring.distributed.scheduler.instruction;

import com.budjb.spring.distributed.scheduler.workload.WorkloadProviderRegistry;
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An {@link Instruction} that requests a {@link WorkloadReport} from
 * a cluster member.
 */
public class ReportInstruction implements Instruction<WorkloadReport> {
    /**
     * Workload provider registry.
     */
    @Autowired
    transient WorkloadProviderRegistry workloadProviderRegistry;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadReport call() {
        return workloadProviderRegistry.getWorkloadReport();
    }
}
