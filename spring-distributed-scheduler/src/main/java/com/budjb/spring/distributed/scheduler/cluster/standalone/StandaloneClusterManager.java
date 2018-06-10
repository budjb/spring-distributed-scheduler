package com.budjb.spring.distributed.scheduler.cluster.standalone;

import com.budjb.spring.distributed.scheduler.instruction.Instruction;
import com.budjb.spring.distributed.scheduler.SchedulerProperties;
import com.budjb.spring.distributed.scheduler.cluster.AbstractClusterManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An implementation of {@link com.budjb.spring.distributed.scheduler.cluster.ClusterManager} that only
 * contains a single node. While this class may seem to violate the purpose of the library, this
 * implementation is useful for local developing and testing.
 */
public class StandaloneClusterManager extends AbstractClusterManager<StandaloneClusterMember> implements ApplicationContextAware {
    /**
     * Executor service.
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Array of cluster members.
     * <p>
     * This will only ever contain one, but we maintain a list to meet the API contract.
     */
    private final List<StandaloneClusterMember> clusterMembers;

    /**
     * Schedule time.
     */
    private long scheduleTime = 0L;
    /**
     * Bean factory.
     */
    private AutowireCapableBeanFactory beanFactory;

    /**
     * Constructor.
     *
     * @param schedulerProperties Scheduler configuration properties.
     */
    public StandaloneClusterManager(SchedulerProperties schedulerProperties) {
        this(schedulerProperties, new StandaloneClusterMember("local"));
    }

    /**
     * Constructor.
     *
     * @param schedulerProperties Scheduler configuration properties.
     * @param member              Cluster member.
     */
    public StandaloneClusterManager(SchedulerProperties schedulerProperties, StandaloneClusterMember member) {
        super(schedulerProperties);
        this.clusterMembers = new ArrayList<>();
        this.clusterMembers.add(member);
    }

    /**
     * Sets the bean factory used to autowire and initialize instructions.
     *
     * @param beanFactory Bean factory instance.
     */
    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected <T> Future<? extends T> submitInstruction(StandaloneClusterMember clusterMember, Instruction<? extends T> instruction) {
        beanFactory.autowireBean(instruction);
        instruction = (Instruction<? extends T>) beanFactory.initializeBean(instruction, instruction.getClass().getName());
        return executorService.submit(instruction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StandaloneClusterMember> getClusterMembers() {
        return clusterMembers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getScheduleTime() {
        return scheduleTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScheduleTime(long time) {
        scheduleTime = time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
    }
}
