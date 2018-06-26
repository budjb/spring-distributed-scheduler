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
 * An extension of {@link Runnable} that provides additional functionality for
 * managing the process logic of a workload.
 */
public interface WorkloadRunnable extends Runnable {
    /**
     * Returns the current state of the runnable.
     *
     * @return the current state of the runnable.
     */
    RunningState getRunningState();

    /**
     * Returns the workload associated with this runnable.
     *
     * @return the workload associated with this runnable.
     */
    Workload getWorkload();

    /**
     * Forcibly sets the runnable to an error state. Also sets the interrupted
     * flag so that the runnable will stop after any in-flight processing.
     * <p>
     * Note that this does not stop execution of the runnable in its thread.
     * This method is intended to be called to true-up the running state in the
     * event that a monitor discovers that this runnable's thread has died.
     */
    void terminate();

    /**
     * Informs the runnable that it should stop processing. The runnable will
     * finish its current update waiting cycle before completing and exiting.
     */
    void interrupt();

    /**
     * Returns whether the runnable should stop processing. This does not indicate
     * its running status. See {@link #getRunningState} for the runnable's actual
     * run state.
     *
     * @return whether the runnable should gracefully discontinue processing.
     */
    boolean isInterrupted();

    /**
     * Forces a failure state. This is useful for testing various failure scenarios.
     */
    void fail();

    /**
     * Returns an exception that was encountered during an error in execution.
     * <p>
     * This will be {@code null} if no errors have occurred.
     *
     * @return the exception thrown when an error occurs, or {@code null} if
     * either there is not one present or the runnable is not in an error state.
     */
    Throwable getException();
}
