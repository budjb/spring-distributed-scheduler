package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.workload.SingleThreadedWorkloadContext
import spock.lang.Specification

class SingleThreadedWorkloadContextSpec extends Specification {
    def 'Attempting to start a context that is already started results in an exception'() {
        setup:
        SingleThreadedWorkloadContext context = new SingleThreadedWorkloadContext(
            new TestRunnable(new TestWorkload('foo'))
        )

        when:
        context.start()

        and:
        context.start()

        then:
        thrown IllegalStateException
    }
}
