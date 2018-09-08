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

package com.budjb.spring.distributed.scheduler.instruction;

import com.budjb.spring.distributed.cluster.Instruction;
import com.budjb.spring.distributed.scheduler.workload.WorkloadContextManager;
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(WorkloadActionsInstruction.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadReport call() {
        try {
            return workloadContextManager.getWorkloadReport();
        }
        catch (Exception e) {
            log.error("Unhandled exception encountered while retrieving report.", e);
            return null;
        }
    }
}
