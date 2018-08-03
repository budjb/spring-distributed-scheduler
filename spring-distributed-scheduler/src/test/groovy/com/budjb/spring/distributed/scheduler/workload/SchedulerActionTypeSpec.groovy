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

import com.budjb.spring.distributed.scheduler.instruction.ActionType
import com.budjb.spring.distributed.scheduler.strategy.SchedulerAction
import spock.lang.Specification

class SchedulerActionTypeSpec extends Specification {
    def 'Getters return the properties the WorkloadAction was build with'() {
        setup:
        Workload workload = Mock(Workload)
        SchedulerAction workloadAction = new SchedulerAction(workload, action)

        expect:
        workloadAction.workload.is(workload)
        workloadAction.actionType == action

        where:
        action << [
            ActionType.ADD,
            ActionType.REMOVE,
            ActionType.RESTART,
            ActionType.FAIL,
        ]

    }
}
