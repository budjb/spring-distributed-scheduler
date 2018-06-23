package com.budjb.spring.distributed.scheduler.workload;

/**
 * Describes a class that provides management of a specific workload type.
 */
public interface WorkloadContextFactory {
    /**
     * Returns whether the workload context factory supports the given workload.
     *
     * @param workload Workload to check support for.
     * @return Whether the workload context factory supports the given workload.
     */
    boolean supports(Workload workload);

    /**
     * Creates a new {@link WorkloadContext} for the given workload.
     *
     * @param workload Workload to create a context for.
     * @return a new {@link WorkloadRunnable} for the given workload.
     */
    WorkloadContext createContext(Workload workload);
}
