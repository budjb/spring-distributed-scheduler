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
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An {@link Instruction} that requests a {@link WorkloadReport} from
 * a cluster member.
 */
public class ReportInstruction implements Instruction<WorkloadReport> {
    /**
     * Workload context manager.
     */
    @Autowired
    transient WorkloadContextManager workloadContextManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadReport call() {
        return workloadContextManager.getWorkloadReport();
    }
}
