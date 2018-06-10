package com.budjb.spring.distributed.scheduler.support.workload

import spock.lang.Specification

class SingleThreadedWorkloadContextSpec extends Specification {
    def 'Attempting to start a context that is already started results in an exception'() {
        setup:
        TestWorkload workload = new TestWorkload('foo')
        TestWorkloadContext context = new TestWorkloadContext(workload)

        when:
        context.start()

        and:
        context.start()

        then:
        thrown IllegalStateException
    }
}
