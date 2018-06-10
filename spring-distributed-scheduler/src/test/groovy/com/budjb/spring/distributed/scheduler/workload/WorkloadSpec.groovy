package com.budjb.spring.distributed.scheduler.workload

import com.budjb.spring.distributed.scheduler.support.workload.Test2Workload
import com.budjb.spring.distributed.scheduler.support.workload.TestWorkload
import spock.lang.Specification

class WorkloadSpec extends Specification {
    def 'When a workload is built with a set of properties, the workload contains them'() {
        when:
        Workload workload = new TestWorkload('foo')

        then:
        workload.urn == 'urn:workload:TestWorkload:foo'
        workload.toString() == 'urn:workload:TestWorkload:foo'
    }

    def 'Validate equals and hashcode behavior between two distinct workloads'() {
        setup:
        Workload a = new TestWorkload(aId)
        Workload b = new TestWorkload(bId)

        expect:
        !a.is(b)
        a.equals(b) == equals
        b.equals(a) == equals
        (a.hashCode() == b.hashCode()) == equals

        where:
        aId   | bId   | equals
        'foo' | 'foo' | true
        'foo' | 'meh' | false
    }

    def 'When two workloads are the identical object, they are equal and their hash codes match'() {
        setup:
        Workload a = new TestWorkload('foo')
        Workload b = a

        expect:
        a.is(b)
        a.equals(b)
        b.equals(a)
        a.hashCode() == b.hashCode()
    }

    def 'When two distinct workloads are not the same object type but otherwise match, they are not equal (even if their hash codes match)'() {
        setup:
        Workload a = new TestWorkload('foo')
        Workload b = new Test2Workload('foo')

        expect:
        !a.is(b)
        !a.equals(b)
        !b.equals(a)
    }
}
