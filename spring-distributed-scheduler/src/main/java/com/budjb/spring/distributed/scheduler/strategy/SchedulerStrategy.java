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
import com.budjb.spring.distributed.scheduler.instruction.WorkloadActionsInstruction;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements the actual logic of creating and updating workload assignments to cluster members.
 */
public interface SchedulerStrategy {
    /**
     * Creates a series of workload actions mapped to cluster members.
     *
     * @param registeredWorkloads A set of workloads that should be scheduled by the cluster.
     * @param reports             A mapping of cluster members to their current work loads.
     * @return a series of change set mappings for cluster members and workloads.
     */
    List<Map<ClusterMember, WorkloadActionsInstruction>> schedule(Set<? extends Workload> registeredWorkloads, Map<? extends ClusterMember, WorkloadReport> reports);
}
