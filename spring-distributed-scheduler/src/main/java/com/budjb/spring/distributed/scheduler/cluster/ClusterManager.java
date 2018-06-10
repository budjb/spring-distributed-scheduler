package com.budjb.spring.distributed.scheduler.cluster;

import com.budjb.spring.distributed.scheduler.instruction.Instruction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Describes a class that allows interaction with other running application nodes
 * that are considered clustered. This functionality serves to help synchronize
 * state between application nodes so that meaningful and stateful load balancing
 * may occur.
 *
 * @param <CM> Cluster member implementation.
 */
public interface ClusterManager<CM extends ClusterMember> {
    /**
     * Returns the set of cluster members that will share workloads.
     *
     * @return the set of cluster members that will share workloads.
     */
    List<CM> getClusterMembers();

    /**
     * Returns the time that the cluster should be scheduled again.
     *
     * @return the time that the cluster should be scheduled again.
     */
    long getScheduleTime();

    /**
     * Sets the time that the cluster should be scheduled again.
     *
     * @param time the time that the cluster should be scheduled again.
     */
    void setScheduleTime(long time);

    /**
     * Submits instructions to members of the cluster.
     *
     * @param instructions instructions to run on cluster members.
     * @param <T> The return type of the instruction.
     * @return the results of the instructions, mapped to the member that generated them.
     * @throws ExecutionException when an error during execution occurs.
     * @throws InterruptedException when the processes is interrupted.
     */
    <T> Map<CM, ? extends T> submitInstructions(Map<CM, ? extends Instruction<? extends T>> instructions) throws ExecutionException, InterruptedException;

    /**
     * Submits an instruction to all cluster members.
     *
     * @param instruction instructions to run on all cluster members.
     * @param <T> The return type of the instruction.
     * @return the results of the instructions, mapped to the member that generated them.
     * @throws ExecutionException when an error during execution occurs.
     * @throws InterruptedException when the processes is interrupted.
     */
    <T> Map<CM, T> submitInstruction(Instruction<? extends T> instruction) throws ExecutionException, InterruptedException;
}