package com.budjb.spring.distributed.scheduler.workload

import com.budjb.spring.distributed.scheduler.SchedulerProperties
import spock.lang.Specification

class WorkloadProviderRegistrySpec extends Specification {
    def 'When a WorkloadProviderRegistry is built with a set of WorkloadProviders, the registry contains them'() {
        setup:
        SchedulerProperties schedulerProperties = new SchedulerProperties();

        WorkloadProvider a = Mock(WorkloadProvider)
        a.getSupportedWorkloadType() >> FooWorkload.class
        WorkloadProvider b = Mock(WorkloadProvider)
        b.getSupportedWorkloadType() >> BarWorkload.class

        when:
        WorkloadProviderRegistry registry = new WorkloadProviderRegistry([a, b], schedulerProperties)

        then:
        registry.workloadProviders.sort() == [a, b].sort()
    }

    def 'When retrieving workloads from a WorkloadProviderRegistry, all workloads from all providers are returned'() {
        setup:
        SchedulerProperties schedulerProperties = new SchedulerProperties();

        Workload one = Mock(Workload)
        Workload two = Mock(Workload)
        Workload three = Mock(Workload)

        WorkloadProvider a = Mock(WorkloadProvider)
        a.getSupportedWorkloadType() >> FooWorkload.class
        a.queryRegisteredWorkloads() >> [one, two]

        WorkloadProvider b = Mock(WorkloadProvider)
        b.getSupportedWorkloadType() >> BarWorkload.class
        b.queryRegisteredWorkloads() >> [three]

        WorkloadProviderRegistry registry = new WorkloadProviderRegistry([a, b], schedulerProperties)

        when:
        Set<Workload> workloads = registry.workloads

        then:
        workloads.sort() == [one, two, three].sort()
    }

    def 'When two workload providers service the same workload type, an exception is thrown'() {
        setup:
        SchedulerProperties schedulerProperties = new SchedulerProperties();

        WorkloadProvider a = Mock(WorkloadProvider)
        a.getSupportedWorkloadType() >> FooWorkload.class
        WorkloadProvider b = Mock(WorkloadProvider)
        b.getSupportedWorkloadType() >> FooWorkload.class

        when:
        new WorkloadProviderRegistry([a, b], schedulerProperties)

        then:
        thrown IllegalArgumentException
    }

    static class FooWorkload extends Workload {
        /**
         * Constructor.
         *
         * @param id ID of the workload.
         */
        protected FooWorkload(String id) {
            super(id)
        }

        @Override
        String getType() {
            return 'foo'
        }
    }

    static class BarWorkload extends Workload {
        /**
         * Constructor.
         *
         * @param id ID of the workload.
         */
        protected BarWorkload(String id) {
            super(id)
        }

        @Override
        String getType() {
            return 'bar'
        }
    }
}
