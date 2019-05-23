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

package com.budjb.spring.distributed.scheduler

import com.budjb.spring.distributed.cluster.ClusterMember
import com.budjb.spring.distributed.cluster.standalone.StandaloneClusterMember
import com.budjb.spring.distributed.scheduler.instruction.ActionType
import com.budjb.spring.distributed.scheduler.instruction.WorkloadActionsInstruction
import com.budjb.spring.distributed.scheduler.strategy.GreedySchedulerStrategy
import com.budjb.spring.distributed.scheduler.strategy.SchedulerStrategy
import com.budjb.spring.distributed.scheduler.support.workload.TestWorkload
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport
import spock.lang.Specification

class GreedySchedulerStrategySpec extends Specification {
    SchedulerStrategy schedulerStrategy

    def setup() {
        schedulerStrategy = new GreedySchedulerStrategy()
    }

    def 'When a cluster contains a single member, and this is the initial scheduling, that member acquires all workloads'() {
        setup:
        TestWorkload wla = new TestWorkload('a')
        TestWorkload wlb = new TestWorkload('b')
        TestWorkload wlc = new TestWorkload('c')

        StandaloneClusterMember member = new StandaloneClusterMember('local')
        WorkloadReport report = new WorkloadReport()

        when:
        List<Map<ClusterMember, WorkloadActionsInstruction>> rounds = schedulerStrategy.schedule([wla, wlb, wlc] as Set, [(member): report])

        then:
        rounds.size() == 1

        when:
        Map<ClusterMember, WorkloadActionsInstruction> round = rounds[0]

        then:
        round.size() == 1
        round.containsKey(member)

        when:
        WorkloadActionsInstruction instruction = round.get(member)

        then:
        instruction.actions.size() == 3
        instruction.actions*.workload.containsAll([wla, wlb, wlc])
        instruction.actions.find { it.workload.is(wla) }.actionType == ActionType.ADD
        instruction.actions.find { it.workload.is(wlb) }.actionType == ActionType.ADD
        instruction.actions.find { it.workload.is(wlc) }.actionType == ActionType.ADD
    }

    def 'When existing workloads have failed and new workloads need to started, appropriate instructions are sent'() {
        setup:
        TestWorkload wla = new TestWorkload('a')
        TestWorkload wlb = new TestWorkload('b')
        TestWorkload wlc = new TestWorkload('c')

        StandaloneClusterMember member = new StandaloneClusterMember('local')

        WorkloadReport.Entry ea = new WorkloadReport.Entry(wla, RunningState.ERROR)
        WorkloadReport.Entry eb = new WorkloadReport.Entry(wlb, RunningState.RUNNING)

        WorkloadReport report = new WorkloadReport()
        report.add(ea)
        report.add(eb)

        when:
        List<Map<ClusterMember, WorkloadActionsInstruction>> rounds = schedulerStrategy.schedule([wla, wlb, wlc] as Set, [(member): report])

        then:
        rounds.size() == 1

        when:
        Map<ClusterMember, WorkloadActionsInstruction> round = rounds[0]

        then:
        round.size() == 1
        round.containsKey(member)

        when:
        WorkloadActionsInstruction instruction = round.get(member)

        then:
        instruction.actions.size() == 2
        instruction.actions*.workload.containsAll([wla, wlc])
        instruction.actions.find { it.workload.is(wla) }.actionType == ActionType.RESTART
        instruction.actions.find { it.workload.is(wlc) }.actionType == ActionType.ADD
    }

    def 'When the registered workloads differ from what is currently running, appropriate instructions are sent'() {
        setup:
        TestWorkload wla = new TestWorkload('a')
        TestWorkload wlb = new TestWorkload('b')
        TestWorkload wlc = new TestWorkload('c')

        StandaloneClusterMember member = new StandaloneClusterMember('local')

        WorkloadReport.Entry ea = new WorkloadReport.Entry(wla, RunningState.RUNNING)
        WorkloadReport.Entry eb = new WorkloadReport.Entry(wlb, RunningState.RUNNING)

        WorkloadReport report = new WorkloadReport()
        report.add(ea)
        report.add(eb)

        when:
        List<Map<ClusterMember, WorkloadActionsInstruction>> rounds = schedulerStrategy.schedule([wla, wlc] as Set, [(member): report])

        then:
        rounds.size() == 2

        when:
        Map<ClusterMember, WorkloadActionsInstruction> round = rounds[0]

        then:
        round.size() == 1
        round.containsKey(member)

        when:
        WorkloadActionsInstruction instruction = round.get(member)

        then:
        instruction.actions.size() == 1
        instruction.actions.get(0).workload.is(wlb)
        instruction.actions.get(0).actionType == ActionType.REMOVE

        when:
        round = rounds[1]

        then:
        round.size() == 1
        round.containsKey(member)

        when:
        instruction = round.get(member)

        then:
        instruction.actions.get(0).workload.is(wlc)
        instruction.actions.get(0).actionType == ActionType.ADD
    }

    def 'When a cluster is out of balance, (preferably terminated) workloads are moved to other members'() {
        setup:
        TestWorkload wla = new TestWorkload('a')
        TestWorkload wlb = new TestWorkload('b')
        TestWorkload wlc = new TestWorkload('c')
        TestWorkload wld = new TestWorkload('d')

        ClusterMember cm1 = Mock(ClusterMember)
        ClusterMember cm2 = Mock(ClusterMember)

        WorkloadReport.Entry ea = new WorkloadReport.Entry(wla, RunningState.RUNNING)
        WorkloadReport.Entry eb = new WorkloadReport.Entry(wlb, RunningState.RUNNING)
        WorkloadReport.Entry ec = new WorkloadReport.Entry(wlc, RunningState.ERROR)
        WorkloadReport.Entry ed = new WorkloadReport.Entry(wld, RunningState.RUNNING)

        WorkloadReport r1 = new WorkloadReport()
        r1.add(ea)

        WorkloadReport r2 = new WorkloadReport()
        r2.add(eb)
        r2.add(ec)
        r2.add(ed)

        when:
        List<Map<ClusterMember, WorkloadActionsInstruction>> rounds = schedulerStrategy.schedule([wla, wlb, wlc, wld] as Set, [(cm1): r1, (cm2): r2])

        then:
        rounds.size() == 2

        when:
        Map<ClusterMember, WorkloadActionsInstruction> round = rounds[0]

        then:
        round.size() == 1
        round.entrySet()[0].key.is(cm2)
        round.entrySet()[0].value.actions.size() == 1
        round.entrySet()[0].value.actions[0].workload.is(wlc)
        round.entrySet()[0].value.actions[0].actionType == ActionType.REMOVE

        when:
        round = rounds[1]

        then:
        round.size() == 1
        round.entrySet()[0].key.is(cm1)
        round.entrySet()[0].value.actions.size() == 1
        round.entrySet()[0].value.actions[0].workload.is(wlc)
        round.entrySet()[0].value.actions[0].actionType == ActionType.ADD
    }

    def 'When a workload exists on multiple cluster members, balancing removes it until it only exists on one'() {
        setup:
        TestWorkload wla = new TestWorkload('a')
        TestWorkload wlb = new TestWorkload('b')
        TestWorkload wlc = new TestWorkload('c')
        TestWorkload wld = new TestWorkload('d')

        ClusterMember cm1 = Mock(ClusterMember)
        cm1.getUrn() >> 'cm1'
        ClusterMember cm2 = Mock(ClusterMember)
        cm2.getUrn() >> 'cm2'
        ClusterMember cm3 = Mock(ClusterMember)
        cm3.getUrn() >> 'cm3'

        WorkloadReport.Entry ea = new WorkloadReport.Entry(wla, RunningState.RUNNING)
        WorkloadReport.Entry eb = new WorkloadReport.Entry(wlb, RunningState.RUNNING)
        WorkloadReport.Entry ec = new WorkloadReport.Entry(wlc, RunningState.RUNNING)
        WorkloadReport.Entry ed = new WorkloadReport.Entry(wld, RunningState.RUNNING)

        WorkloadReport r1 = new WorkloadReport()
        r1.add(ea)
        r1.add(eb)

        WorkloadReport r2 = new WorkloadReport()
        r2.add(eb)
        r2.add(ec)

        WorkloadReport r3 = new WorkloadReport()
        r3.add(eb)
        r3.add(ed)

        when:
        List<Map<ClusterMember, WorkloadActionsInstruction>> rounds = schedulerStrategy.schedule([wla, wlb, wlc, wld] as Set, [(cm1): r1, (cm2): r2, (cm3): r3])

        then:
        rounds.size() == 1

        when:
        Map<ClusterMember, WorkloadActionsInstruction> round = rounds[0]

        then:
        round.size() == 2
        round.entrySet()*.key.toSet() == (Set) [cm2, cm3]
        round.entrySet()*.value*.actions*.size() == [1, 1]
        round.entrySet()*.value*.actions*.workload == [[wlb], [wlb]]
        round.entrySet()*.value*.actions*.actionType == [[ActionType.REMOVE], [ActionType.REMOVE]]
    }
}
