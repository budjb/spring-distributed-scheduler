package com.budjb.spring.distributed.scheduler

import com.budjb.spring.distributed.cluster.ClusterManager
import com.budjb.spring.distributed.cluster.ClusterMember
import com.budjb.spring.distributed.lock.DistributedLock
import com.budjb.spring.distributed.lock.DistributedLockProvider
import com.budjb.spring.distributed.scheduler.instruction.ReportInstruction
import com.budjb.spring.distributed.scheduler.instruction.SchedulerInstruction
import com.budjb.spring.distributed.scheduler.strategy.SchedulerStrategy
import com.budjb.spring.distributed.scheduler.workload.Workload
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport
import com.budjb.spring.distributed.scheduler.workload.WorkloadRepository
import org.junit.Ignore
import spock.lang.Specification

class DistributedSchedulerSpec extends Specification {
    DistributedLockProvider distributedLockProvider
    ClusterManager clusterManager
    SchedulerProperties schedulerProperties
    SchedulerStrategy schedulerStrategy
    DistributedScheduler distributedScheduler
    WorkloadRepository workloadRepository

    def setup() {
        distributedLockProvider = Mock(DistributedLockProvider)
        clusterManager = Mock(ClusterManager)
        workloadRepository = Mock(WorkloadRepository)
        schedulerProperties = Mock(SchedulerProperties)
        schedulerStrategy = Mock(SchedulerStrategy)

        distributedScheduler = new DistributedScheduler(distributedLockProvider, clusterManager, schedulerProperties, schedulerStrategy, workloadRepository)
    }

    def 'When the application starts, a forced schedule occurs'() {
        setup:
        DistributedLock lock = Mock(DistributedLock)
        distributedLockProvider.getDistributedLock((String) _) >> lock

        when:
        distributedScheduler.onStart()

        then:
        1 * lock.tryLock(*_) >> false
    }

    def 'When it\'s time to check if a schedule needs to occur, a non-forced schedule happens'() {
        setup:
        DistributedLock lock = Mock(DistributedLock)
        lock.tryLock(*_) >> true
        distributedLockProvider.getDistributedLock((String) _) >> lock

        clusterManager.getProperty('schedule-time', Long) >> System.currentTimeMillis() + 10000

        when:
        distributedScheduler.schedule()

        then:
        1 * lock.unlock()
        0 * workloadRepository.getWorkloads()
        0 * clusterManager.submitInstruction(*_)
    }

    @Ignore
    def 'When it\'s not time for a schedule to occur but it is forced, the scheduler runs'() {
        setup:
        DistributedLock lock = Mock(DistributedLock)
        lock.tryLock(*_) >> true
        distributedLockProvider.getDistributedLock((String) _) >> lock

        ClusterMember clusterMember = Mock(ClusterMember)
        WorkloadReport workloadReport = Mock(WorkloadReport)
        Workload workload = Mock(Workload)
        Set<Workload> workloads = [workload]
        workloadRepository.getWorkloads() >> workloads

        Map<ClusterMember, WorkloadReport> reports = [(clusterMember): workloadReport]
        clusterManager.submitInstruction({ it instanceof ReportInstruction }) >> reports
        clusterManager.getProperty('schedule-time', Long) >> System.currentTimeMillis() + 10000

        SchedulerInstruction workloadActionsInstruction = Mock(SchedulerInstruction)
        Map<ClusterMember, SchedulerInstruction> instructionSet = [(clusterMember): workloadActionsInstruction]

        schedulerStrategy.schedule(workloads, reports) >> [instructionSet]

        when:
        distributedScheduler.schedule(true)

        then:
        1 * lock.unlock()
        1 * clusterManager.submitInstructions(instructionSet)
        1 * clusterManager.setProperty('schedule-time', _)
    }

    def 'When it\'s time for a schedule to occur, instructions are compiled and sent to cluster members'() {
        setup:
        DistributedLock lock = Mock(DistributedLock)
        lock.tryLock(*_) >> true
        distributedLockProvider.getDistributedLock((String) _) >> lock

        ClusterMember clusterMember = Mock(ClusterMember)
        WorkloadReport workloadReport = Mock(WorkloadReport)
        Workload workload = Mock(Workload)
        Set<Workload> workloads = [workload]
        workloadRepository.getWorkloads() >> workloads

        Map<ClusterMember, WorkloadReport> reports = [(clusterMember): workloadReport]
        clusterManager.submitInstruction({ it instanceof ReportInstruction }) >> reports
        clusterManager.getProperty('schedule-time', Long) >> 0L

        SchedulerInstruction workloadActionsInstruction = Mock(SchedulerInstruction)
        Map<ClusterMember, SchedulerInstruction> instructionSet = [(clusterMember): workloadActionsInstruction]

        schedulerStrategy.schedule(workloads, reports) >> [instructionSet]

        when:
        distributedScheduler.schedule()

        then:
        1 * lock.unlock()
        1 * clusterManager.submitInstructions(instructionSet)
        1 * clusterManager.setProperty('schedule-time', _)
    }

    def 'If no workload reports received, an IllegalStateException is thrown, swallowed, and logged, and nothing is scheduled'() {
        setup:
        DistributedLock lock = Mock(DistributedLock)
        lock.tryLock(*_) >> true
        distributedLockProvider.getDistributedLock((String) _) >> lock

        clusterManager.submitInstruction({ it instanceof ReportInstruction }) >> [:]

        when:
        distributedScheduler.schedule(true)

        then:
        1 * workloadRepository.getWorkloads()
        1 * lock.unlock()
        0 * schedulerStrategy.schedule(*_)
    }
}
