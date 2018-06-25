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

package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.workload.SingleThreadedWorkloadContext
import com.budjb.spring.distributed.scheduler.workload.Workload
import com.budjb.spring.distributed.scheduler.workload.WorkloadContext
import com.budjb.spring.distributed.scheduler.workload.WorkloadContextFactory

class TestWorkloadContextFactory implements WorkloadContextFactory {
    @Override
    WorkloadContext createContext(Workload workload) {
        return new SingleThreadedWorkloadContext(new TestRunnable(workload))
    }

    @Override
    boolean supports(Workload workload) {
        return workload instanceof TestWorkload
    }
}
