package com.budjb.spring.distributed.scheduler;

import com.budjb.spring.distributed.scheduler.instruction.Instruction;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * A set of configuration properties used to manage the execution of {@link Instruction} instances.
 */
@Validated
public class ClusterInstructionProperties {
    /**
     * The amount of time that the cluster manager should wait for all published
     * instructions to complete before giving up (in milliseconds).
     */
    @Min(0)
    private long managerTimeout = 150000L;

    /**
     * The amount of time that the instruction worker should wait for the instruction
     * logic to complete before giving up (in milliseconds).
     */
    @Min(0)
    private long pollTimeout = 120000L;

    /**
     * The amount of time that the instruction worker should wait before checking whether
     * the instruction logic has completed (in milliseconds).
     */
    @Min(0)
    private long pollInterval = 250L;

    public long getPollTimeout() {
        return pollTimeout;
    }

    public void setPollTimeout(long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    public long getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(long pollInterval) {
        this.pollInterval = pollInterval;
    }

    public long getManagerTimeout() {
        return managerTimeout;
    }

    public void setManagerTimeout(long managerTimeout) {
        this.managerTimeout = managerTimeout;
    }
}
