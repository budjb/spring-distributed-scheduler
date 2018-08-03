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

import com.budjb.spring.distributed.scheduler.RunningState
import spock.lang.Specification

class WorkloadReportSpec extends Specification {
    def 'When a report is constructed with entries, the report contains them'() {
        setup:
        Workload a = Mock(Workload)
        Workload b = Mock(Workload)
        WorkloadReport.Entry entryA = new WorkloadReport.Entry(a, RunningState.RUNNING)
        WorkloadReport.Entry entryB = new WorkloadReport.Entry(b, RunningState.ERROR)

        when:
        WorkloadReport report = new WorkloadReport([entryA, entryB])

        then:
        report.entries == [entryA, entryB]
    }

    def 'When an entry is added to an existing report, the report contains it'() {
        setup:
        Workload a = Mock(Workload)
        Workload b = Mock(Workload)
        WorkloadReport.Entry entryA = new WorkloadReport.Entry(a, RunningState.RUNNING)
        WorkloadReport.Entry entryB = new WorkloadReport.Entry(b, RunningState.ERROR)
        WorkloadReport report = new WorkloadReport()

        when:
        report.add(entryA)
        report.add(entryB)

        then:
        report.entries == [entryA, entryB]
    }

    def 'When an entry is built, its getters reflect what was given to the constructor'() {
        setup:
        Workload workload = Mock(Workload)
        Exception exception = new RuntimeException("test exception")

        when:
        WorkloadReport.Entry entry = new WorkloadReport.Entry(workload, RunningState.RUNNING)

        then:
        entry.workload.is(workload)
        entry.state == RunningState.RUNNING
        entry.error == null

        when:
        entry = new WorkloadReport.Entry(workload, RunningState.RUNNING, exception)

        then:
        entry.workload.is(workload)
        entry.state == RunningState.RUNNING
        entry.error == 'test exception'
    }
}
