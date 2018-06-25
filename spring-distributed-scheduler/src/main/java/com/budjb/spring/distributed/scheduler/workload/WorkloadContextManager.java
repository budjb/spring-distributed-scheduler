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

import com.budjb.spring.distributed.scheduler.SchedulerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Responsible for containing and managing running workloads via {@link WorkloadContext workload contexts} created
 * by {@link WorkloadContextFactory workload factories}. Managing the lifecycle of a workload through the manager
 * only interacts with workload contexts running on locally running application node, and not the cluster.
 * Therefore, this class should be treated as an internal component and not interacted with directly.
 */
public class WorkloadContextManager {
    /**
     * Logger.
     */
    private final Logger log = LoggerFactory.getLogger(WorkloadContextManager.class);

    /**
     * Executors service.
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Workload context factories.
     */
    private final List<WorkloadContextFactory> workloadContextFactories;

    /**
     * Scheduler properties.
     */
    private final SchedulerProperties schedulerProperties;

    /**
     * Workload contexts.
     */
    private final List<WorkloadContext> workloadContexts = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param workloadContextFactories The set of available workload context factories.
     * @param schedulerProperties      Scheduler properties.
     */
    public WorkloadContextManager(List<WorkloadContextFactory> workloadContextFactories, SchedulerProperties schedulerProperties) {
        this.workloadContextFactories = workloadContextFactories;
        this.schedulerProperties = schedulerProperties;
    }

    /**
     * Returns all managed workload contexts being.
     *
     * @return All managed workload contexts being.
     */
    public Collection<WorkloadContext> getWorkloadContexts() {
        return workloadContexts;
    }

    /**
     * Returns the set of registered workload context factories.
     *
     * @return The set of registered workload context factories.
     */
    public Collection<WorkloadContextFactory> getWorkloadContextFactories() {
        return workloadContextFactories;
    }

    /**
     * Creates a workload report containing entries for all workloads.
     *
     * @return A complete workload report.
     */
    public WorkloadReport getWorkloadReport() {
        return new WorkloadReport(getWorkloadContexts().stream().map(WorkloadContext::getWorkloadReportEntry).collect(Collectors.toList()));
    }

    /**
     * Returns the set of workload context factories that support the given workload.
     *
     * @param workload Workload to get factories for.
     * @return The set of workload context factories that support the given workload.
     */
    private List<WorkloadContextFactory> findSupportingContextFactories(Workload workload) {
        return workloadContextFactories.stream().filter(f -> f.supports(workload)).collect(Collectors.toList());
    }

    /**
     * Starts the given workload.
     *
     * @param workload Workload to start.
     */
    public void start(Workload workload) {
        findSupportingContextFactories(workload).forEach(f -> {
            WorkloadContext workloadContext = f.createContext(workload);
            workloadContexts.add(workloadContext);
            workloadContext.start();
        });
    }

    /**
     * Stops and removes the given workload.
     *
     * @param workload Workload to remove.
     * @return A future for the process of removing the workload.
     */
    public Future remove(Workload workload) {
        List<WorkloadContext> contexts = getContexts(workload);
        this.workloadContexts.removeAll(contexts);
        return stop(contexts);
    }

    /**
     * Stops the given workload.
     *
     * @param workload Workload to stop.
     * @return A future for the process of stopping the workload.
     */
    public Future stop(Workload workload) {
        return stop(getContexts(workload));
    }

    /**
     * Restarts the given workload.
     *
     * @param workload Workload to restart.
     * @return A future for the process of restarting the workload.
     */
    public Future restart(Workload workload) {
        return executorService.submit(() -> {
            Future future = remove(workload);

            while (!future.isDone()) {
                try {
                    future.get();
                }
                catch (InterruptedException ignored) {
                    log.error("interrupted while restarting workload " + workload.getUrn());
                    return;
                }
                catch (ExecutionException e) {
                    log.error("unexpected execution exception while attempting to restart workload " + workload.getUrn());
                    return;
                }
            }

            start(workload);
        });
    }

    /**
     * Shuts down all workloads.
     *
     * @return A future to track its execution state.
     */
    public Future shutdown() {
        return stop(workloadContexts);
    }

    /**
     * Stops and forces an error state on the given workload.
     *
     * @param workload Workload to fail.
     */
    public void fail(Workload workload) {
        fail(getContexts(workload));
    }

    /**
     * Returns whether the manager is service the given workload (i.e. is there at least
     * one context with the given workload).
     *
     * @param workload Workload to check.
     * @return Whether the manager is service the given workload.
     */
    public boolean isServicing(Workload workload) {
        return workloadContexts.stream().anyMatch(c -> c.getWorkload().equals(workload));
    }

    /**
     * Returns all workload contexts associated with the given workload.
     *
     * @param workload Workload to retrieve contexts for.
     * @return All workload contexts associated with the given workload.
     */
    private List<WorkloadContext> getContexts(Workload workload) {
        return workloadContexts.stream().filter(c -> c.getWorkload().equals(workload)).collect(Collectors.toList());
    }

    /**
     * Stops the given list of workload contexts.
     *
     * @param contexts Workload contexts to stop.
     * @return A future to track completion of the workload context.
     */
    private Future stop(List<WorkloadContext> contexts) {
        return executorService.submit(() -> {
            List<Future> futures = contexts.stream().map(this::stop).collect(Collectors.toList());

            try {
                do {
                    futures.removeIf(Future::isDone);
                    Thread.sleep(schedulerProperties.getActionPollInterval());
                }
                while (futures.size() > 0);
            }
            catch (InterruptedException ignored) {
                log.error("interrupted while stopping workloads");
            }
        });
    }

    /**
     * Stops the given workload context.
     *
     * @param context Workload context to stop.
     * @return A future for the process of stopping the workload context.
     */
    private Future stop(WorkloadContext context) {
        return executorService.submit(() -> {
            context.stop();

            long end = System.currentTimeMillis() + schedulerProperties.getActionPollTimeout();

            while (System.currentTimeMillis() < end) {
                if (context.isStopped()) {
                    return;
                }
                try {
                    Thread.sleep(schedulerProperties.getActionPollInterval());
                }
                catch (InterruptedException ignored) {
                    log.error("interrupted while stopping workload " + context.getWorkload().getUrn());
                }
            }

            if (!context.isStopped()) {
                context.terminate();
            }
        });
    }

    /**
     * Stops and forces an error state on the given list of workload contexts.
     *
     * @param contexts Workload contexts to fail.
     */
    private void fail(List<WorkloadContext> contexts) {
        contexts.forEach(this::fail);
    }

    /**
     * Stops and forces an error state on the given workload context.
     *
     * @param context Workload context to fail.
     */
    private void fail(WorkloadContext context) {
        context.fail();
    }
}
