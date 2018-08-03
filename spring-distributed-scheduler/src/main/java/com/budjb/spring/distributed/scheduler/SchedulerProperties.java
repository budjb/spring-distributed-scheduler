/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.budjb.spring.distributed.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * A set of configuration properties used to manage the cluster.
 */
@Validated
@ConfigurationProperties("scheduler")
public class SchedulerProperties {
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
    /**
     * The amount of time that the worker context manager should wait for the instruction
     * logic to complete before giving up (in milliseconds).
     */
    @Min(0)
    private long actionPollTimeout = 120000L;
    /**
     * The amount of time that the worker context manager should wait before polling whether
     * the instruction logic has completed (in milliseconds).
     */
    @Min(0)
    private long actionPollInterval = 250L;

    public long getActionPollTimeout() {
        return actionPollTimeout;
    }

    public void setActionPollTimeout(long actionPollTimeout) {
        this.actionPollTimeout = actionPollTimeout;
    }

    public long getActionPollInterval() {
        return actionPollInterval;
    }

    public void setActionPollInterval(long actionPollInterval) {
        this.actionPollInterval = actionPollInterval;
    }

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

}
