package com.budjb.spring.distributed.scheduler;

import com.budjb.spring.distributed.lock.DistributedLockProvider;
import com.budjb.spring.distributed.scheduler.cluster.ClusterManager;
import com.budjb.spring.distributed.scheduler.cluster.ClusterMember;
import com.budjb.spring.distributed.scheduler.cluster.standalone.StandaloneClusterManager;
import com.budjb.spring.distributed.scheduler.cluster.standalone.StandaloneClusterMember;
import com.budjb.spring.distributed.scheduler.strategy.GreedySchedulerStrategy;
import com.budjb.spring.distributed.scheduler.strategy.SchedulerStrategy;
import com.budjb.spring.distributed.scheduler.workload.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DistributedSchedulerAutoConfiguration {
    @Bean
    public SchedulerProperties schedulerProperties() {
        return new SchedulerProperties();
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public WorkloadContextManager workloadContextManager(List<WorkloadContextFactory> workloadContextFactories, SchedulerProperties schedulerProperties) {
        return new WorkloadContextManager(workloadContextFactories, schedulerProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DistributedScheduler distributedScheduler(DistributedLockProvider distributedLockProvider, ClusterManager<ClusterMember> clusterManager, SchedulerProperties schedulerProperties, SchedulerStrategy schedulerStrategy, WorkloadRepository workloadRepository) {
        return new DistributedScheduler(distributedLockProvider, clusterManager, schedulerProperties, schedulerStrategy, workloadRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerStrategy schedulerStrategy() {
        return new GreedySchedulerStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClusterManager clusterManager(SchedulerProperties schedulerProperties) {
        return new StandaloneClusterManager(schedulerProperties, new StandaloneClusterMember("local"));
    }

    @Bean
    @ConditionalOnMissingBean
    public WorkloadRepository workloadRepository(List<WorkloadRepositorySource> sources) {
        return new CachingWorkloadRepository(sources);
    }
}
