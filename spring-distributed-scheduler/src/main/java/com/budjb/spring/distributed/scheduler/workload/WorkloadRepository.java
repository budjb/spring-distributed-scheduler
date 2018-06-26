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
