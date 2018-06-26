/*
 * Copyright 2018 Bud Byrd
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

package com.budjb.spring.distributed.scheduler;

import com.budjb.spring.distributed.cluster.ClusterManager;
import com.budjb.spring.distributed.lock.DistributedLockProvider;
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
    public DistributedScheduler distributedScheduler(DistributedLockProvider distributedLockProvider, ClusterManager clusterManager, SchedulerProperties schedulerProperties, SchedulerStrategy schedulerStrategy, WorkloadRepository workloadRepository) {
        return new DistributedScheduler(distributedLockProvider, clusterManager, schedulerProperties, schedulerStrategy, workloadRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerStrategy schedulerStrategy() {
        return new GreedySchedulerStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public WorkloadRepository workloadRepository(List<WorkloadRepositorySource> sources) {
        return new CachingWorkloadRepository(sources);
    }

    @Bean
    WorkloadLifecycleHelper workloadLifecycleHelper(ClusterManager clusterManager, WorkloadRepository workloadRepository) {
        return new WorkloadLifecycleHelper(clusterManager, workloadRepository);
    }
}
