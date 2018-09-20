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

import java.time.Duration

class SchedulerPropertiesSpec extends Specification {
    def 'Default properties are correct'() {
        setup:
        SchedulerProperties properties = new SchedulerProperties()

        expect:
        properties.rebalanceInterval.toMillis() == 180000L
        properties.rebalancePollDelay.toMillis() == 30000L
        properties.rebalancePollInterval.toMillis() == 30000L
    }

    def 'Overridden properties are correct'() {
        setup:
        SchedulerProperties properties = new SchedulerProperties()
        properties.rebalanceInterval = Duration.ofMillis(1)
        properties.rebalancePollDelay = Duration.ofMillis(3)
        properties.rebalancePollInterval = Duration.ofMillis(4)

        expect:
        properties.rebalanceInterval.toMillis() == 1
        properties.rebalancePollDelay.toMillis() == 3
        properties.rebalancePollInterval.toMillis() == 4
    }
}
