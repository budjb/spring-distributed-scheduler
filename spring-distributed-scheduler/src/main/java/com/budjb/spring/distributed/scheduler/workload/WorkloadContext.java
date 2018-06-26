/*
 * Copyright 2018 Bud Byrd
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

package com.budjb.spring.distributed.scheduler.workload;

import com.budjb.spring.distributed.scheduler.RunningState;

/**
 * Manages the runtime of a specific workload.
 */
public interface WorkloadContext {
    /**
     * Returns the workload context acts on.
     *
     * @return the workload context acts on.
     */
    Workload getWorkload();

    /**
     * Generates a workload report entry for the workload being processed by this context.
     *
     * @return a workload report entry.
     */
    WorkloadReport.Entry getWorkloadReportEntry();

    /**
     * Starts the workload.
     */
    void start();

    /**
     * Signals that the workload should stop and gracefully terminate. This call is non-blocking
     * and does not wait for the workload to stop. Running status can subsequently be determined
     * through the use of {@link #getRunningState} and {@link #isStopped}.
     */
    void stop();

    /**
     * Returns whether the workload has stopped. A runnable is considered stopped when
     * its thread has died or its running state is terminated.
     *
     * @return whether the runnable has stopped.
     */
    boolean isStopped();

    /**
     * Stops execution of the workload's thread. This should only be used as
     * a last-ditch attempt to get the workload to stop collecting.
     */
    void terminate();

    /**
     * Interrupts and forces an error state on the workload. This is useful for testing.
     */
    void fail();

    /**
     * Returns the running state of the workload.
     *
     * @return the running state of the workload.
     */
    RunningState getRunningState();
}