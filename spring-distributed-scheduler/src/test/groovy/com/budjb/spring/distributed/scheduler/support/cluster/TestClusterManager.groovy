package com.budjb.spring.distributed.scheduler.support.cluster

import com.budjb.spring.distributed.scheduler.instruction.Instruction
import com.budjb.spring.distributed.scheduler.SchedulerProperties
import com.budjb.spring.distributed.scheduler.cluster.AbstractClusterManager
import com.budjb.spring.distributed.scheduler.cluster.ClusterMember

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class TestClusterManager extends AbstractClusterManager<TestClusterMember> {
    long scheduleTime
    List<TestClusterMember> clusterMembers
    Map<ClusterMember, List<Instruction<?>>> instructions = [:]

    TestClusterManager(SchedulerProperties schedulerProperties) {
        super(schedulerProperties)
    }

    @Override
    <T> Future<T> submitInstruction(TestClusterMember clusterMember, Instruction<? extends T> instruction) {
        if (!instructions.containsKey(clusterMember)) {
            instructions.put(clusterMember, [])
        }

        instructions.get(clusterMember).add(instruction)

        if (instruction instanceof TestInstruction && instruction.future != null) {
            return instruction.future
        }
        else {
            CompletableFuture<T> future = new CompletableFuture<>()
            future.complete(null)
            return future
        }
    }
}
