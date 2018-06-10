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
