package com.budjb.spring.distributed.scheduler.workload

import com.budjb.spring.distributed.scheduler.strategy.ActionType
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
            ActionType.SIMULATE_FAIL,
        ]

    }
}
