package com.budjb.spring.distributed.scheduler;

import com.budjb.spring.distributed.lock.DistributedLockProvider;
import com.budjb.spring.distributed.scheduler.cluster.ClusterManager;
import com.budjb.spring.distributed.scheduler.cluster.ClusterMember;
import com.budjb.spring.distributed.scheduler.strategy.GreedySchedulerStrategy;
import com.budjb.spring.distributed.scheduler.strategy.SchedulerStrategy;
import com.budjb.spring.distributed.scheduler.workload.WorkloadProvider;
import com.budjb.spring.distributed.scheduler.workload.WorkloadProviderRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DistributedSchedulerAutoConfiguration {
    @Bean
    SchedulerProperties schedulerProperties() {
        return new SchedulerProperties();
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    WorkloadProviderRegistry workloadProviderRegistry(List<WorkloadProvider> workloadProviders, SchedulerProperties schedulerProperties) {
        return new WorkloadProviderRegistry(workloadProviders, schedulerProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    DistributedScheduler distributedScheduler(DistributedLockProvider distributedLockProvider, WorkloadProviderRegistry workloadProviderRegistry, ClusterManager<ClusterMember> clusterManager, SchedulerProperties schedulerProperties, SchedulerStrategy schedulerStrategy) {
        return new DistributedScheduler(distributedLockProvider, workloadProviderRegistry, clusterManager, schedulerProperties, schedulerStrategy);
    }

    @Bean
    @ConditionalOnMissingBean
    SchedulerStrategy schedulerStrategy() {
        return new GreedySchedulerStrategy();
    }
}
