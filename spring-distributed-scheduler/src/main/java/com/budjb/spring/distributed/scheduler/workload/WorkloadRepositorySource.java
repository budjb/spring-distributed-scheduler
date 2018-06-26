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
 * A workload repository source is responsible for providing sets of workloads that
 * the schedule should act upon. Any implementations of this interface will be used
 * by the {@link WorkloadRepository} to aggregate a set of workloads while will be
 * scheduled for execution by the application cluster.
 * <p>
 * There is no assumption that implementation will cache the results of the
 * implementation-specific query. For example, if an implementation must query a
 * database or some other web service, it is left to the implementation to determine
 * whether those results should be cached for a time.
 * <p>
 * As a note, the default {@link WorkloadRepository} registered in Spring is a
 * {@link CachingWorkloadRepository}, which caches the results from all repository
 * source implementations at an aggregated level.
 */
public interface WorkloadRepositorySource {
    Set<Workload> queryWorkloads();
}
