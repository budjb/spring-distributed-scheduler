package com.budjb.spring.distributed.scheduler.workload

import com.budjb.spring.distributed.scheduler.instruction.SchedulerInstruction
import com.budjb.spring.distributed.scheduler.instruction.ActionType
import com.budjb.spring.distributed.scheduler.strategy.SchedulerAction
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

class SchedulerActionsInstructionSpec extends Specification {
    def 'When a WorkloadActionsInstruction is built with actions, it contains those actions'() {
        setup:
        SchedulerAction a = new SchedulerAction(null, null)
        SchedulerAction b = new SchedulerAction(null, null)

        when:
        SchedulerInstruction instruction = new SchedulerInstruction([a, b])

        then:
        instruction.actions.size() == 2
        instruction.actions.get(0).is(a)
        instruction.actions.get(1).is(b)
    }

    def 'Appropriate methods are called for the various action types'() {
        setup:
        Future<?> future = new CompletableFuture<>()
        future.complete(null)

        SchedulerAction a = new SchedulerAction(Mock(Workload), ActionType.FAIL)
        SchedulerAction b = new SchedulerAction(Mock(Workload), ActionType.ADD)
        SchedulerAction c = new SchedulerAction(Mock(Workload), ActionType.RESTART)
        SchedulerAction d = new SchedulerAction(Mock(Workload), ActionType.REMOVE)

        WorkloadContextManager workloadContextManager = Mock(WorkloadContextManager)

        SchedulerInstruction instruction = new SchedulerInstruction([a, b, c, d])
        instruction.workloadContextManager = workloadContextManager

        when:
        instruction.call()

        then:
        1 * workloadContextManager.start(_)
        1 * workloadContextManager.stop(_) >> future
        1 * workloadContextManager.restart(_) >> future
        1 * workloadContextManager.fail(_)
    }
}
