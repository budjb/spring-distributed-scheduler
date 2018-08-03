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
