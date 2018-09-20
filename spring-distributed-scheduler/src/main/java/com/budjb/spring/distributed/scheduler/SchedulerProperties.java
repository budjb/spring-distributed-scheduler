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

import java.time.Duration;

/**
 * A set of configuration properties used to manage the cluster.
 */
@Validated
@ConfigurationProperties("scheduler")
public class SchedulerProperties {
    /**
     * The amount of time that should pass between cluster re-balancing, in milliseconds.
     */
    private Duration rebalanceInterval = Duration.ofMinutes(3);
    /**
     * The amount of time that the application should wait until it starts checking whether
     * a re-balance should occur, in milliseconds.
     */
    private Duration rebalancePollDelay = Duration.ofSeconds(30);
    /**
     * The amount of time that should pass between checks to determine whether a re-rebalance
     * should occur, in milliseconds.
     */
    private Duration rebalancePollInterval = Duration.ofSeconds(30);
    /**
     * The amount of time that the worker context manager should wait for the instruction
     * logic to complete before giving up (in milliseconds).
     */
    private Duration actionPollTimeout = Duration.ofMinutes(2);
    /**
     * The amount of time that the worker context manager should wait before polling whether
     * the instruction logic has completed (in milliseconds).
     */
    private Duration actionPollInterval = Duration.ofMillis(250);

    public Duration getActionPollTimeout() {
        return actionPollTimeout;
    }

    public void setActionPollTimeout(Duration actionPollTimeout) {
        this.actionPollTimeout = actionPollTimeout;
    }

    public Duration getActionPollInterval() {
        return actionPollInterval;
    }

    public void setActionPollInterval(Duration actionPollInterval) {
        this.actionPollInterval = actionPollInterval;
    }

    public Duration getRebalancePollDelay() {
        return rebalancePollDelay;
    }

    public void setRebalancePollDelay(Duration rebalancePollDelay) {
        this.rebalancePollDelay = rebalancePollDelay;
    }

    public Duration getRebalancePollInterval() {
        return rebalancePollInterval;
    }

    public void setRebalancePollInterval(Duration rebalancePollInterval) {
        this.rebalancePollInterval = rebalancePollInterval;
    }

    public Duration getRebalanceInterval() {
        return rebalanceInterval;
    }

    public void setRebalanceInterval(Duration rebalanceInterval) {
        this.rebalanceInterval = rebalanceInterval;
    }

}
