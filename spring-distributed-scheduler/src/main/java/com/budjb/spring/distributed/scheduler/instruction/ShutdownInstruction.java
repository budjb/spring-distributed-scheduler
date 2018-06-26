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

package com.budjb.spring.distributed.scheduler.instruction;

import com.budjb.spring.distributed.cluster.Instruction;
import com.budjb.spring.distributed.scheduler.workload.WorkloadContextManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Future;

/**
 * Instructs a cluster to stop all running workloads.
 */
public class ShutdownInstruction implements Instruction<Void> {
    @Autowired
    transient WorkloadContextManager workloadContextManager;

    @Override
    public Void call() throws Exception {
        Future future = workloadContextManager.shutdown();
        future.get();
        return null;
    }
}
