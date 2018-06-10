package com.budjb.spring.distributed.scheduler

import spock.lang.Specification

class ClusterInstructionPropertiesSpec extends Specification {
    def 'Default properties are correct'() {
        setup:
        ClusterInstructionProperties properties = new ClusterInstructionProperties()

        expect:
        properties.pollTimeout == 120000L
        properties.pollInterval == 250L
        properties.managerTimeout == 150000L
    }

    def 'Overridden properties are correct'() {
        setup:
        ClusterInstructionProperties properties = new ClusterInstructionProperties()
        properties.pollTimeout = 1
        properties.pollInterval = 2
        properties.managerTimeout = 3

        expect:
        properties.pollTimeout == 1
        properties.pollInterval == 2
        properties.managerTimeout == 3
    }
}
