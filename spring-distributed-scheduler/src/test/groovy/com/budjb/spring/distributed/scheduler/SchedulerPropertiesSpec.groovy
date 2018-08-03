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

package com.budjb.spring.distributed.scheduler

import spock.lang.Specification

class SchedulerPropertiesSpec extends Specification {
    def 'Default properties are correct'() {
        setup:
        SchedulerProperties properties = new SchedulerProperties()

        expect:
        properties.rebalanceInterval == 180000L
        properties.rebalancePollDelay == 30000L
        properties.rebalancePollInterval == 30000L
    }

    def 'Overridden properties are correct'() {
        setup:
        SchedulerProperties properties = new SchedulerProperties()
        properties.rebalanceInterval = 1
        properties.rebalancePollDelay = 3
        properties.rebalancePollInterval = 4

        expect:
        properties.rebalanceInterval == 1
        properties.rebalancePollDelay == 3
        properties.rebalancePollInterval == 4
    }
}
