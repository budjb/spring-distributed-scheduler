package com.budjb.spring.distributed.scheduler.workload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple implementation of {@link WorkloadRepository} that queries {@link WorkloadRepositorySource} beans for
 * workloads.
 */
public class SimpleWorkloadRepository implements WorkloadRepository {
    /**
     * Workload repository sources.
     */
    private final List<WorkloadRepositorySource> sources;

    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(SimpleWorkloadRepository.class);

    /**
     * Constructor.
     *
     * @param sources Workload repository source beans.
     */
    public SimpleWorkloadRepository(List<WorkloadRepositorySource> sources) {
        this.sources = sources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Workload lookup(String urn) {
        return getWorkloads().stream().filter(w -> w.getUrn().equals(urn)).findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Workload> getWorkloads() {
        Set<Workload> workloads = new HashSet<>();

        for (WorkloadRepositorySource source : sources) {
            try {
                workloads.addAll(source.queryWorkloads());
            }
            catch (Exception e) {
                log.error("unhandled exception encountered while querying a workload repository source", e);
            }
        }

        return workloads;
    }

    /**
     * Returns the list of registered {@link WorkloadRepositorySource} beans.
     *
     * @return The list of registered {@link WorkloadRepositorySource} beans.
     */
    protected List<WorkloadRepositorySource> getSources() {
        return sources;
    }
}
