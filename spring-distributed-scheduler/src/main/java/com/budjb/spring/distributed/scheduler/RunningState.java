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

/**
 * Represents the various states that the processing of a workload can take.
 */
public enum RunningState {
    NOT_STARTED(false, false),
    STARTING(false, false),
    RUNNING(false, false),
    STOPPING(true, false),
    STOPPED(true, true),
    ERROR(true, true);

    /**
     * Whether the running state is considered terminal; i.e., the workload is not yet
     * stopped but is in the processing of stopping.
     */
    private final boolean terminal;

    /**
     * Whether the running state represents a state where the workload has finished
     * its processing, whether by success or failure.
     */
    private final boolean terminated;

    /**
     * Constructor.
     *
     * @param terminal Whether the state is terminal.
     * @param terminated Whether the state represents a workload that has terminated.
     */
    RunningState(boolean terminal, boolean terminated) {
        this.terminal = terminal;
        this.terminated = terminated;
    }

    /**
     * Returns whether the state is terminal.
     *
     * @return Whether the state is terminal.
     */
    public boolean isTerminal() {
        return terminal;
    }

    /**
     * Returns whether the state represents a workload that has terminated.
     * @return Whether the state represents a workload that has terminated.
     */
    public boolean isTerminated() {
        return terminated;
    }
}
