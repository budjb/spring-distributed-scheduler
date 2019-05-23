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

package com.budjb.spring.distributed.scheduler.strategy;

import com.budjb.spring.distributed.cluster.ClusterMember;
import com.budjb.spring.distributed.scheduler.instruction.WorkloadActionsInstruction;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An implementation of a scheduler strategy that attempts to spread workloads evenly
 * across cluster members using a greedy algorithm.
 */
public class GreedySchedulerStrategy extends AbstractSchedulerStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<ClusterMember, WorkloadActionsInstruction>> schedule(Set<? extends Workload> registeredWorkloads, Map<? extends ClusterMember, WorkloadReport> reports) {
        // Create the scheduler context.
        SchedulerStrategyContext context = new SchedulerStrategyContext(copyReports(reports));

        // Determine the set of existing workloads.
        Set<Workload> existingWorkloads = reports.values().stream().flatMap(report -> report.getEntries().stream()).map(WorkloadReport.Entry::getWorkload).collect(Collectors.toSet());

        // Determine the set of new workloads.
        Set<Workload> newWorkloads = registeredWorkloads.stream().filter(workload -> !existingWorkloads.contains(workload)).collect(Collectors.toSet());

        // Determine the set of orphaned workloads.
        Set<Workload> orphanedWorkloads = existingWorkloads.stream().filter(workload -> !registeredWorkloads.contains(workload)).collect(Collectors.toSet());

        // De-schedule orphaned workloads.
        orphanedWorkloads.forEach(w -> removeWorkload(context, w));

        // Find duplicate workloads.
        // Note that this should not occur under normal circumstances
        // but may happen when service discovery is lost.
        registeredWorkloads.forEach(w -> {
            boolean found = false;

            for (ClusterMember clusterMember : reports.keySet()) {
                WorkloadReport report = reports.get(clusterMember);

                if (report.getEntries().stream().anyMatch(e -> e.getWorkload().getUrn().equals(w.getUrn()))) {
                    if (!found) {
                        found = true;
                    }
                    else {
                        removeWorkload(context, clusterMember, w);
                    }
                }
            }
        });

        // Schedule new workloads on nodes with the least load.
        newWorkloads.forEach(w -> addWorkload(context, findLeastBusyMember(context).getKey(), w));

        // Distribute workload from over-burdened cluster members to others with low load.
        while (true) {
            Map.Entry<? extends ClusterMember, WorkloadReport> low = findLeastBusyMember(context);
            Map.Entry<? extends ClusterMember, WorkloadReport> high = findMostBusyMember(context);

            int delta = high.getValue().getEntries().size() - low.getValue().getEntries().size();

            if (delta < 2) {
                break;
            }

            Workload candidate = high.getValue().getEntries().stream().min(new EntryRunningStateComparator()).map(WorkloadReport.Entry::getWorkload).orElse(null);

            removeWorkload(context, high.getKey(), candidate);
            addWorkload(context, low.getKey(), candidate);
        }

        // Restart failed workloads.
        context.getMapping().forEach((k, v) -> v.getEntries().stream().filter(e -> e.getState().isTerminated()).map(WorkloadReport.Entry::getWorkload).distinct().forEach(w -> restartWorkload(context, k, w)));

        return toInstructionMap(context);
    }

    /**
     * Finds the cluster member with the least amount of load.
     *
     * @param context Scheduler strategy context.
     * @return the cluster member with the least amount of load.
     */
    private Map.Entry<? extends ClusterMember, WorkloadReport> findLeastBusyMember(SchedulerStrategyContext context) {
        return context.getMapping().entrySet().stream().min(Comparator.comparing(entry -> entry.getValue().getEntries().size())).orElse(null);
    }

    /**
     * Finds the cluster member with the most amount of load.
     *
     * @param context Scheduler strategy context.
     * @return the cluster member with the most amount of load.
     */
    private Map.Entry<? extends ClusterMember, WorkloadReport> findMostBusyMember(SchedulerStrategyContext context) {
        return context.getMapping().entrySet().stream().max(Comparator.comparing(entry -> entry.getValue().getEntries().size())).orElse(null);
    }
}
