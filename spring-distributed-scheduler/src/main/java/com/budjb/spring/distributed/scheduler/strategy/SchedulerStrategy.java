package com.budjb.spring.distributed.scheduler.strategy;

import com.budjb.spring.distributed.cluster.ClusterMember;
import com.budjb.spring.distributed.scheduler.instruction.SchedulerInstruction;
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
     * Creates a series of change set mappings for cluster members and workloads.
     *
     * @param registeredWorkloads A set of workloads that should be scheduled by the cluster.
     * @param reports             A mapping of cluster members to their current work loads.
     * @return a series of change set mappings for cluster members and workloads.
     */
    List<Map<ClusterMember, SchedulerInstruction>> schedule(Set<? extends Workload> registeredWorkloads, Map<? extends ClusterMember, WorkloadReport> reports);
}
