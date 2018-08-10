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

package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.RunningState
import com.budjb.spring.distributed.scheduler.workload.AbstractWorkloadRunnable
import com.budjb.spring.distributed.scheduler.workload.Workload

class TestRunnable extends AbstractWorkloadRunnable {
    /**
     * Constructor.
     *
     * @param workload Workload that this runnable services.
     */
    TestRunnable(Workload workload) {
        super(workload)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void run() {
        setRunningState(RunningState.RUNNING)

        while (!Thread.interrupted()) {
            try {
                Thread.sleep(250)
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt()
            }
        }

        if (!getRunningState().isTerminated()) {
            setRunningState(RunningState.STOPPED)
        }
    }
}
