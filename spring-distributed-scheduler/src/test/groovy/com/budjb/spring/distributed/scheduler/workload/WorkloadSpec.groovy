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
