package com.budjb.spring.distributed.scheduler.workload;

import java.util.Set;

/**
 * Describes a class that provides management of a specific workload type.
 */
public interface WorkloadProvider {
    /**
     * Returns the workload class type the provider supports.
     *
     * @return the workload class type the provider supports.
     */
    Class<? extends Workload> getSupportedWorkloadType();

    /**
     * Creates a new {@link WorkloadRunnable} for the given workload.
     *
     * @param workload Workload to create a context for.
     * @return a new {@link WorkloadRunnable} for the given workload.
     */
    WorkloadContext registerWorkload(Workload workload);

    /**
     * Returns the set of workload contexts currently managed by the provider.
     *
     * @return the set of workload contexts currently managed by the provider.
     */
    Set<WorkloadContext> getContexts();

    /**
     * Returns the context associated with the given workload.
     *
     * @param workload Workload to query the context for.
     * @return the context associated with the given workload.
     */
    WorkloadContext getContext(Workload workload);

    /**
     * Returns the context associated with the given workload. The context
     * is also removed from the provider and is no longer managed. Responsibility
     * of managing the context is transferred to the caller.
     *
     * @param workload Workload to query the context for.
     * @return the context associated with the given workload.
     */
    WorkloadContext removeContext(Workload workload);

    /**
     * Returns the set of workload units associated with this provider.
     *
     * @return the set of workload units associated with this provider.
     */
    Set<Workload> queryRegisteredWorkloads();

    /**
     * Removes and returns all workloads.
     *
     * @return All workloads.
     */
    Set<WorkloadContext> removeAll();
}
