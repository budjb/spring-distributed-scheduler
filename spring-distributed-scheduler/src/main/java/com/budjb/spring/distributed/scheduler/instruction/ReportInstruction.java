package com.budjb.spring.distributed.scheduler.instruction;

import com.budjb.spring.distributed.scheduler.workload.WorkloadContextManager;
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An {@link Instruction} that requests a {@link WorkloadReport} from
 * a cluster member.
 */
public class ReportInstruction implements Instruction<WorkloadReport> {
    /**
     * Workload context manager.
     */
    @Autowired
    transient WorkloadContextManager workloadContextManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadReport call() {
        return workloadContextManager.getWorkloadReport();
    }
}
