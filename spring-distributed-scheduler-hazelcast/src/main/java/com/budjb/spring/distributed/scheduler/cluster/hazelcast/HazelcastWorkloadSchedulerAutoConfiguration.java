package com.budjb.spring.distributed.scheduler.cluster.hazelcast;

import com.budjb.spring.distributed.scheduler.DistributedSchedulerAutoConfiguration;
import com.budjb.spring.distributed.scheduler.SchedulerProperties;
import com.budjb.spring.distributed.scheduler.cluster.ClusterManager;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(DistributedSchedulerAutoConfiguration.class)
class HazelcastWorkloadSchedulerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    ClusterManager clusterManager(HazelcastInstance hazelcastInstance, SchedulerProperties schedulerProperties) {
        return new HazelcastClusterManager(hazelcastInstance, schedulerProperties);
    }
}
