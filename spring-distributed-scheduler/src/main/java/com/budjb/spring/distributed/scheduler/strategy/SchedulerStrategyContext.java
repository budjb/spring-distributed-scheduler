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

package com.budjb.spring.distributed.scheduler.strategy;

import com.budjb.spring.distributed.cluster.ClusterMember;
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class that contains the mapping of cluster members to workloads and
 * a list of scheduler actions to submit to cluster members.
 */
public class SchedulerStrategyContext {
    /**
     * Mapping of cluster members to workload reports. This set of data is intended
     * to be mutable by the scheduling process, and serve as a way to define the
     * ideal state of the cluster as the scheduling algorithm is run.
     */
    private final Map<? extends ClusterMember, WorkloadReport> mapping;

    /**
     * Mapping of cluster members to actions they should perform.
     */
    private final Map<ClusterMember, List<SchedulerAction>> actions = new HashMap<>();

    /**
     * Container map.
     *
     * @param mapping mapping of cluster members to their workload report.
     */
    public SchedulerStrategyContext(Map<? extends ClusterMember, WorkloadReport> mapping) {
        this.mapping = mapping;
    }

    /**
     * Returns the mapping of cluster members to workload reports.
     *
     * @return the mapping of cluster members to workload reports.
     */
    public Map<? extends ClusterMember, WorkloadReport> getMapping() {
        return mapping;
    }

    /**
     * Returns the mapping of cluster members to actions they should perform.
     *
     * @return the mapping of cluster members to actions they should perform.
     */
    public Map<ClusterMember, List<SchedulerAction>> getActions() {
        return actions;
    }
}
