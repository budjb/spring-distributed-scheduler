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

package com.budjb.spring.distributed.scheduler.workload

import com.budjb.spring.distributed.scheduler.instruction.WorkloadActionsInstruction
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
        WorkloadActionsInstruction instruction = new WorkloadActionsInstruction([a, b])

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
        SchedulerAction e = new SchedulerAction(Mock(Workload), ActionType.STOP)

        WorkloadContextManager workloadContextManager = Mock(WorkloadContextManager)
        workloadContextManager.isServicing(_) >> true

        WorkloadActionsInstruction instruction = new WorkloadActionsInstruction([a, b, c, d, e])
        instruction.workloadContextManager = workloadContextManager

        when:
        instruction.call()

        then:
        1 * workloadContextManager.start(_)
        1 * workloadContextManager.remove(_) >> future
        1 * workloadContextManager.stop(_) >> future
        1 * workloadContextManager.restart(_) >> future
        1 * workloadContextManager.fail(_)
    }
}
