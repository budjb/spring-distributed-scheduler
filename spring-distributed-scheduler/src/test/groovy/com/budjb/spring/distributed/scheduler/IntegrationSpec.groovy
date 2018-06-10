package com.budjb.spring.distributed.scheduler

import com.budjb.spring.distributed.lock.DistributedLockProvider
import com.budjb.spring.distributed.lock.reentrant.ReentrantDistributedLockProvider
import com.budjb.spring.distributed.scheduler.cluster.standalone.StandaloneClusterManager
import com.budjb.spring.distributed.scheduler.instruction.SchedulerInstruction
import com.budjb.spring.distributed.scheduler.strategy.*
import com.budjb.spring.distributed.scheduler.support.workload.TestWorkload
import com.budjb.spring.distributed.scheduler.support.workload.TestWorkloadProvider
import com.budjb.spring.distributed.scheduler.workload.Workload
import com.budjb.spring.distributed.scheduler.workload.WorkloadProvider
import com.budjb.spring.distributed.scheduler.workload.WorkloadProviderRegistry
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import spock.lang.Specification

class IntegrationSpec extends Specification {
    DefaultListableBeanFactory beanFactory
    DistributedLockProvider distributedLockProvider
    SchedulerProperties schedulerProperties
    StandaloneClusterManager clusterManager
    SchedulerStrategy schedulerStrategy

    def setup() {
        beanFactory = new DefaultListableBeanFactory()
        AutowiredAnnotationBeanPostProcessor postProcessor = new AutowiredAnnotationBeanPostProcessor()
        postProcessor.setBeanFactory(beanFactory)
        beanFactory.addBeanPostProcessor(postProcessor)

        distributedLockProvider = new ReentrantDistributedLockProvider()

        schedulerProperties = new SchedulerProperties()
        clusterManager = new StandaloneClusterManager(schedulerProperties)
        clusterManager.setBeanFactory(beanFactory)

        schedulerStrategy = new GreedySchedulerStrategy()
    }

    def 'All workloads are running after the initial scheduling'() {
        setup:
        Workload a = new TestWorkload('a')
        Workload b = new TestWorkload('b')
        Workload c = new TestWorkload('c')
        Workload d = new TestWorkload('d')

        WorkloadProvider workloadProvider = new TestWorkloadProvider([a, b, c, d] as Set)

        WorkloadProviderRegistry workloadProviderRegistry = new WorkloadProviderRegistry([workloadProvider], schedulerProperties)
        beanFactory.registerSingleton('workloadProviderRegistry', workloadProviderRegistry)

        DistributedScheduler distributedScheduler = new DistributedScheduler(distributedLockProvider, workloadProviderRegistry, clusterManager, schedulerProperties, schedulerStrategy)

        when: 'the initial schedule occurs'
        distributedScheduler.schedule(true)

        and: 'the report is gathered'
        WorkloadReport report = workloadProviderRegistry.getWorkloadReport()

        then: 'all workloads are running'
        report.entries.size() == 4
        report.entries*.workload.sort() == [a, b, c, d].sort()
        report.entries*.state.every { it == RunningState.RUNNING }

        cleanup:
        distributedScheduler.shutdown()
    }

    def 'A workload fails and is restarted automatically'() {
        setup:
        Workload a = new TestWorkload('a')
        Workload b = new TestWorkload('b')
        Workload c = new TestWorkload('c')
        Workload d = new TestWorkload('d')

        WorkloadProvider workloadProvider = new TestWorkloadProvider([a, b, c, d] as Set)

        WorkloadProviderRegistry workloadProviderRegistry = new WorkloadProviderRegistry([workloadProvider], schedulerProperties)
        beanFactory.registerSingleton('workloadProviderRegistry', workloadProviderRegistry)

        DistributedScheduler distributedScheduler = new DistributedScheduler(distributedLockProvider, workloadProviderRegistry, clusterManager, schedulerProperties, schedulerStrategy)

        distributedScheduler.schedule(true)

        when: 'workload "A" has failed'
        clusterManager.submitInstructions([(clusterManager.getClusterMembers()[0]): new SchedulerInstruction([new SchedulerAction(a, ActionType.SIMULATE_FAIL)])])

        and: 'the report is retrieved'
        WorkloadReport report = workloadProviderRegistry.getWorkloadReport()

        then: 'workload "A" is reported as failed and the other 3 are still running'
        report.entries.size() == 4
        report.entries.find { it.workload == a }.state == RunningState.ERROR

        when: 'a re-schedule occurs'
        distributedScheduler.schedule(true)

        and: 'the report is refreshed'
        report = workloadProviderRegistry.getWorkloadReport()

        then: 'workload "A" has been restarted'
        report.entries.size() == 4
        report.entries.find { it.workload == a }.state == RunningState.RUNNING

        cleanup:
        distributedScheduler.shutdown()
    }

    def 'A workload is removed from the registry and then reintroduced'() {
        setup:
        Workload a = new TestWorkload('a')
        Workload b = new TestWorkload('b')
        Workload c = new TestWorkload('c')
        Workload d = new TestWorkload('d')

        WorkloadProvider workloadProvider = new TestWorkloadProvider([a, b, c, d] as Set)

        WorkloadProviderRegistry workloadProviderRegistry = new WorkloadProviderRegistry([workloadProvider], schedulerProperties)
        beanFactory.registerSingleton('workloadProviderRegistry', workloadProviderRegistry)

        DistributedScheduler distributedScheduler = new DistributedScheduler(distributedLockProvider, workloadProviderRegistry, clusterManager, schedulerProperties, schedulerStrategy)

        distributedScheduler.schedule(true)

        when: 'workload "c" is removed from the registry'
        workloadProvider.removeWorkload(c)

        and: 'a re-schedule occurs'
        distributedScheduler.schedule(true)

        and: 'the report is retrieved'
        WorkloadReport report = workloadProviderRegistry.getWorkloadReport()

        then: 'workload "C" is no longer reported'
        report.entries.size() == 3
        !report.entries.any { it.workload == c }

        when: 'workload "C" is reintroduced into the registry'
        workloadProvider.addWorkload(c)

        and: 'a re-schedule occurs'
        distributedScheduler.schedule(true)

        and: 'the report is refreshed'
        report = workloadProviderRegistry.getWorkloadReport()

        then: 'workload "C" is reported again'
        report.entries.size() == 4
        report.entries.find { it.workload == c }

        cleanup:
        distributedScheduler.shutdown()
    }
}
