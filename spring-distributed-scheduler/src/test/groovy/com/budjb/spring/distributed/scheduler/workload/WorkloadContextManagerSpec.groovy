package com.budjb.spring.distributed.scheduler.workload

import com.budjb.spring.distributed.scheduler.SchedulerProperties
import spock.lang.Specification

class WorkloadContextManagerSpec extends Specification {
    def 'When a WorkloadContextManager is built with a set of WorkloadContextFactories, the registry contains them'() {
        setup:
        SchedulerProperties schedulerProperties = new SchedulerProperties();

        WorkloadContextFactory a = Mock(WorkloadContextFactory)
        WorkloadContextFactory b = Mock(WorkloadContextFactory)

        when:
        WorkloadContextManager workloadContextManager = new WorkloadContextManager([a, b], schedulerProperties)

        then:
        workloadContextManager.workloadContextFactories.sort() == [a, b].sort()
    }
}
