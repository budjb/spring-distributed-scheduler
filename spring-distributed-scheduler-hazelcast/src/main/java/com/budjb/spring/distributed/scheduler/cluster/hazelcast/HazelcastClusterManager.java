package com.budjb.spring.distributed.scheduler.cluster.hazelcast;

import com.budjb.spring.distributed.scheduler.instruction.Instruction;
import com.budjb.spring.distributed.scheduler.SchedulerProperties;
import com.budjb.spring.distributed.scheduler.cluster.AbstractClusterManager;
import com.budjb.spring.distributed.scheduler.cluster.ClusterManager;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * A {@link ClusterManager} implementation backed by Hazelcast.
 */
public class HazelcastClusterManager extends AbstractClusterManager<HazelcastClusterMember> implements InitializingBean {
    /**
     * Name of the key in the {@link #HAZELCAST_MAP_NAME} com.budjb.spring.lock.distributed map.
     */
    private final static String RE_BALANCE_KEY = "re-balance";

    /**
     * Name of the com.budjb.spring.lock.distributed map to store time markers.
     */
    private final static String HAZELCAST_MAP_NAME = "time-markers";

    /**
     * Name of the executor service.
     */
    private final static String EXECUTOR_NAME = "cluster-management";

    /**
     * Hazelcast instance.
     */
    private final HazelcastInstance hazelcastInstance;

    /**
     * Hazelcast executor service.
     */
    private IExecutorService executorService;

    /**
     * Constructor.
     *
     * @param hazelcastInstance
     */
    public HazelcastClusterManager(HazelcastInstance hazelcastInstance, SchedulerProperties schedulerProperties) {
        super(schedulerProperties);
        this.hazelcastInstance = hazelcastInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HazelcastClusterMember> getClusterMembers() {
        return hazelcastInstance.getCluster().getMembers().stream().map(HazelcastClusterMember::new).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getScheduleTime() {
        Map<String, Long> timeMarkers = hazelcastInstance.getMap(HAZELCAST_MAP_NAME);

        if (!timeMarkers.containsKey(RE_BALANCE_KEY)) {
            return 0;
        }

        return timeMarkers.get(RE_BALANCE_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScheduleTime(long time) {
        hazelcastInstance.getMap(HAZELCAST_MAP_NAME).put(RE_BALANCE_KEY, System.currentTimeMillis() + schedulerProperties.getRebalanceInterval());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> Future<T> submitInstruction(HazelcastClusterMember clusterMember, Instruction<? extends T> instruction) {
        return executorService.submit(
            new AutowiringCallableWrapper<T>(instruction),
            new SingleMemberSelector(clusterMember.getMember())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = hazelcastInstance.getExecutorService(EXECUTOR_NAME);
    }

    /**
     * A Hazelcast selector that identifies a singular member.
     */
    private static class SingleMemberSelector implements MemberSelector {
        /**
         * Hazelcast cluster member.
         */
        final Member member;

        /**
         * Constructor.
         *
         * @param member
         */
        public SingleMemberSelector(Member member) {
            this.member = member;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean select(Member member) {
            return this.member == member;
        }
    }
}
