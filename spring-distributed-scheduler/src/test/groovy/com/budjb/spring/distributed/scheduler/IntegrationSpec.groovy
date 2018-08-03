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

import com.budjb.spring.distributed.cluster.ClusterConfigurationProperties
import com.budjb.spring.distributed.cluster.standalone.StandaloneClusterManager
import com.budjb.spring.distributed.lock.DistributedLockProvider
import com.budjb.spring.distributed.lock.reentrant.ReentrantDistributedLockProvider
import com.budjb.spring.distributed.scheduler.instruction.WorkloadActionsInstruction
import com.budjb.spring.distributed.scheduler.instruction.ActionType
import com.budjb.spring.distributed.scheduler.strategy.GreedySchedulerStrategy
import com.budjb.spring.distributed.scheduler.strategy.SchedulerAction
import com.budjb.spring.distributed.scheduler.strategy.SchedulerStrategy
import com.budjb.spring.distributed.scheduler.support.workload.TestWorkload
import com.budjb.spring.distributed.scheduler.support.workload.TestWorkloadContextFactory
import com.budjb.spring.distributed.scheduler.support.workload.TestWorkloadRepositorySource
import com.budjb.spring.distributed.scheduler.workload.*
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import spock.lang.Specification

class IntegrationSpec extends Specification {
    DefaultListableBeanFactory beanFactory
    DistributedLockProvider distributedLockProvider
    SchedulerProperties schedulerProperties
    ClusterConfigurationProperties clusterConfigurationProperties
    StandaloneClusterManager clusterManager
    SchedulerStrategy schedulerStrategy

    def setup() {
        beanFactory = new DefaultListableBeanFactory()
        AutowiredAnnotationBeanPostProcessor postProcessor = new AutowiredAnnotationBeanPostProcessor()
        postProcessor.setBeanFactory(beanFactory)
        beanFactory.addBeanPostProcessor(postProcessor)

        distributedLockProvider = new ReentrantDistributedLockProvider()

        schedulerProperties = new SchedulerProperties()
        clusterConfigurationProperties = new ClusterConfigurationProperties()
        clusterManager = new StandaloneClusterManager(clusterConfigurationProperties)
        clusterManager.setBeanFactory(beanFactory)

        schedulerStrategy = new GreedySchedulerStrategy()
    }

    def 'All workloads are running after the initial scheduling'() {
        setup:
        Workload a = new TestWorkload('a')
        Workload b = new TestWorkload('b')
        Workload c = new TestWorkload('c')
        Workload d = new TestWorkload('d')

        WorkloadRepositorySource workloadRepositorySource = new TestWorkloadRepositorySource([a, b, c, d] as Set)
        WorkloadRepository workloadRepository = new SimpleWorkloadRepository([workloadRepositorySource])

        WorkloadContextFactory workloadContextFactory = new TestWorkloadContextFactory()

        WorkloadContextManager workloadContextManager = new WorkloadContextManager([workloadContextFactory], schedulerProperties)
        beanFactory.registerSingleton('workloadContextManager', workloadContextManager)

        DistributedScheduler distributedScheduler = new DistributedScheduler(distributedLockProvider, clusterManager, schedulerProperties, schedulerStrategy, workloadRepository)

        when: 'the initial schedule occurs'
        distributedScheduler.schedule(true)

        and: 'some time has passed to avoid a race condition'
        sleep(50)

        and: 'the report is gathered'
        WorkloadReport report = workloadContextManager.getWorkloadReport()

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

        WorkloadRepositorySource workloadRepositorySource = new TestWorkloadRepositorySource([a, b, c, d] as Set)
        WorkloadRepository workloadRepository = new SimpleWorkloadRepository([workloadRepositorySource])

        WorkloadContextFactory workloadContextFactory = new TestWorkloadContextFactory()

        WorkloadContextManager workloadContextManager = new WorkloadContextManager([workloadContextFactory], schedulerProperties)
        beanFactory.registerSingleton('workloadContextManager', workloadContextManager)

        DistributedScheduler distributedScheduler = new DistributedScheduler(distributedLockProvider, clusterManager, schedulerProperties, schedulerStrategy, workloadRepository)

        distributedScheduler.schedule(true)

        when: 'workload "A" has failed'
        clusterManager.submitInstructions([(clusterManager.getClusterMembers()[0]): new WorkloadActionsInstruction([new SchedulerAction(a, ActionType.FAIL)])])

        and: 'the report is retrieved'
        WorkloadReport report = workloadContextManager.getWorkloadReport()

        then: 'workload "A" is reported as failed and the other 3 are still running'
        report.entries.size() == 4
        report.entries.find { it.workload == a }.state == RunningState.ERROR

        when: 'a re-schedule occurs'
        distributedScheduler.schedule(true)

        and: 'the report is refreshed'
        report = workloadContextManager.getWorkloadReport()

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

        WorkloadRepositorySource workloadRepositorySource = new TestWorkloadRepositorySource([a, b, c, d] as Set)
        WorkloadRepository workloadRepository = new SimpleWorkloadRepository([workloadRepositorySource])

        WorkloadContextFactory workloadContextFactory = new TestWorkloadContextFactory()

        WorkloadContextManager workloadContextManager = new WorkloadContextManager([workloadContextFactory], schedulerProperties)
        beanFactory.registerSingleton('workloadContextManager', workloadContextManager)

        DistributedScheduler distributedScheduler = new DistributedScheduler(distributedLockProvider, clusterManager, schedulerProperties, schedulerStrategy, workloadRepository)

        distributedScheduler.schedule(true)

        when: 'workload "c" is removed from the registry'
        workloadRepositorySource.removeWorkload(c)

        and: 'a re-schedule occurs'
        distributedScheduler.schedule(true)

        and: 'the report is retrieved'
        WorkloadReport report = workloadContextManager.getWorkloadReport()

        then: 'workload "C" is no longer reported'
        report.entries.size() == 3
        !report.entries.any { it.workload == c }

        when: 'workload "C" is reintroduced into the registry'
        workloadRepositorySource.addWorkload(c)

        and: 'a re-schedule occurs'
        distributedScheduler.schedule(true)

        and: 'the report is refreshed'
        report = workloadContextManager.getWorkloadReport()

        then: 'workload "C" is reported again'
        report.entries.size() == 4
        report.entries.find { it.workload == c }

        cleanup:
        distributedScheduler.shutdown()
    }
}
