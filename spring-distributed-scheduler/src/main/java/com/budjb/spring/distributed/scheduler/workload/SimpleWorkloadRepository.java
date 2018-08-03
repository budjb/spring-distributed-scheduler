/*
 * Copyright 2018 the original author or authors.
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
