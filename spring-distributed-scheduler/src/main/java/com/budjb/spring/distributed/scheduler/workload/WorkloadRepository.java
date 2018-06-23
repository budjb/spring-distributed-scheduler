package com.budjb.spring.distributed.scheduler.workload;

import java.util.Set;

/**
 * Responsible for aggregating {@link Workload workloads} from all available {@link WorkloadRepositorySource} beans.
 */
public interface WorkloadRepository {
    /**
     * Attempts to find a {@link Workload} with the given URN and returns it. If one could not be found, {@code null}
     * is returned.
     *
     * @param urn URN of the workload.
     * @return The workload associated with the requested URN, or {@code null}.
     */
    Workload lookup(String urn);

    /**
     * Returns all workloads that the scheduler should act upon.
     * <p>
     * Workloads are queried and aggregated from all {@link WorkloadRepositorySource workload repository sources} beans.
     *
     * @return All workloads that the scheduler should act upon.
     */
    Set<Workload> getWorkloads();
}
