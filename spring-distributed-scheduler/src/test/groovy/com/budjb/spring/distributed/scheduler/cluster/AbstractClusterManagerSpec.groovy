package com.budjb.spring.distributed.scheduler.cluster

import com.budjb.spring.distributed.scheduler.instruction.Instruction
import com.budjb.spring.distributed.scheduler.SchedulerProperties
import com.budjb.spring.distributed.scheduler.support.cluster.TestClusterManager
import com.budjb.spring.distributed.scheduler.support.cluster.TestClusterMember
import com.budjb.spring.distributed.scheduler.support.cluster.TestInstruction
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class AbstractClusterManagerSpec extends Specification {
    SchedulerProperties schedulerProperties
    TestClusterManager clusterManager

    def setup() {
        schedulerProperties = new SchedulerProperties()
        clusterManager = new TestClusterManager(schedulerProperties)
    }

    def 'When only an instruction is submitted, it is submitted to all cluster members'() {
        setup:
        TestClusterMember a = new TestClusterMember('a')
        TestClusterMember b = new TestClusterMember('b')

        clusterManager.clusterMembers = [a, b]

        Instruction instruction = Mock(Instruction)

        when:
        clusterManager.submitInstruction(instruction)

        then:
        clusterManager.instructions == [
            (a): [instruction],
            (b): [instruction]
        ]
    }

    def 'When an instruction takes too long, an error is logged and its results are not returned'() {
        setup:
        schedulerProperties.instruction.pollTimeout = 1L
        TestClusterMember a = new TestClusterMember('a')
        TestClusterMember b = new TestClusterMember('b')

        clusterManager.clusterMembers = [a, b]

        CompletableFuture future = new CompletableFuture()
        TestInstruction instructionA = new TestInstruction()
        instructionA.future = future
        TestInstruction instructionB = new TestInstruction()

        when:
        Map<ClusterMember, Void> results = clusterManager.submitInstructions([(a): instructionA, (b): instructionB])

        then:
        results.containsKey(b)
        !results.containsKey(a)
    }
}
