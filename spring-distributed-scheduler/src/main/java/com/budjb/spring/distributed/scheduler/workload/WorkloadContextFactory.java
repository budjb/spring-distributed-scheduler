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

/**
 * Describes a factory for workload contexts.
 * <p>
 * Implementations of this interface provide the glue between a workload (a unit of work)
 * and some logic that acts on the workload. The factory is responsible for creating
 * a workload context that is specific to some executor that knows how to handle the workload
 * type.
 * <p>
 * There is no assumption that a workload type
 * maps to only one context factory; in fact, more that one factory may support a single
 * workload type.
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
