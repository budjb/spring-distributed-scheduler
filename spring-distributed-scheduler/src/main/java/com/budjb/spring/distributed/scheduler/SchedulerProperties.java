package com.budjb.spring.distributed.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * A set of configuration properties used to manage the cluster.
 */
@Validated
@ConfigurationProperties("scheduler")
public class SchedulerProperties {
    @Valid
    @NestedConfigurationProperty
    private final ClusterInstructionProperties instruction = new ClusterInstructionProperties();

    /**
     * The amount of time that should pass between cluster re-balancing, in milliseconds.
     */
    @Min(0)
    private long rebalanceInterval = 180000L;

    /**
     * The amount of time that the application should wait until it starts checking whether
     * a re-balance should occur, in milliseconds.
     */
    @Min(0)
    private long rebalancePollDelay = 30000L;

    /**
     * The amount of time that should pass between checks to determine whether a re-rebalance
     * should occur, in milliseconds.
     */
    @Min(0)
    private long rebalancePollInterval = 30000L;

    public long getRebalancePollDelay() {
        return rebalancePollDelay;
    }

    public void setRebalancePollDelay(long rebalancePollDelay) {
        this.rebalancePollDelay = rebalancePollDelay;
    }

    public long getRebalancePollInterval() {
        return rebalancePollInterval;
    }

    public void setRebalancePollInterval(long rebalancePollInterval) {
        this.rebalancePollInterval = rebalancePollInterval;
    }

    public long getRebalanceInterval() {
        return rebalanceInterval;
    }

    public void setRebalanceInterval(long rebalanceInterval) {
        this.rebalanceInterval = rebalanceInterval;
    }

    public ClusterInstructionProperties getInstruction() {
        return instruction;
    }

}
