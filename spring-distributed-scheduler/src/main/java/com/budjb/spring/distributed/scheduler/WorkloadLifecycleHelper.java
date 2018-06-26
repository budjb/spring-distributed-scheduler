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

package com.budjb.spring.distributed.scheduler;

import com.budjb.spring.distributed.cluster.ClusterManager;
import com.budjb.spring.distributed.scheduler.instruction.WorkloadActionsInstruction;
import com.budjb.spring.distributed.scheduler.instruction.ActionType;
import com.budjb.spring.distributed.scheduler.strategy.SchedulerAction;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Provides a way to influence the workload lifecycle during the application runtime.
 * Note that changes made to the cluster workload state through this class will, by
 * its very nature, automatically be cleaned up by the scheduler.
 */
public class WorkloadLifecycleHelper {
    /**
     * Cluster manager.
     */
    private final ClusterManager clusterManager;

    /**
     * Workload repository.
     */
    private final WorkloadRepository workloadRepository;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(WorkloadLifecycleHelper.class);

    /**
     * Constructor.
     *
     * @param clusterManager     Cluster manager.
     * @param workloadRepository Workload repository.
     */
    public WorkloadLifecycleHelper(ClusterManager clusterManager, WorkloadRepository workloadRepository) {
        this.clusterManager = clusterManager;
        this.workloadRepository = workloadRepository;
    }

    /**
     * Stops the given workload, regardless of which cluster member is running it.
     *
     * @param workload Workload to stop.
     */
    public void stop(Workload workload) {
        try {
            submitInstruction(workload, ActionType.STOP);
        }
        catch (ExecutionException e) {
            log.error("unhandled exception while stopping workload " + workload.getUrn(), e);
        }
        catch (InterruptedException ignored) {
            log.error("interrupted while waiting for workload " + workload.getUrn() + " to stop");
        }
    }

    /**
     * Stops the given workload, regardless of which cluster member is running it.
     *
     * @param urn URN of the workload to stop.
     */
    public void stop(String urn) {
        Workload workload = getWorkload(urn);

        if (workload == null) {
            throw new IllegalArgumentException("could not locate a workload with the URN " + urn);
        }

        stop(workload);
    }

    /**
     * Interrupts and forces an error on the given workload, regardless of which cluster member is running it.
     *
     * @param urn URN of the workload to fail.
     */
    public void fail(String urn) {
        Workload workload = getWorkload(urn);

        if (workload == null) {
            throw new IllegalArgumentException("could not locate a workload with the URN " + urn);
        }

        fail(workload);
    }

    /**
     * Interrupts and forces an error on the given workload, regardless of which cluster member is running it.
     *
     * @param workload Workload to fail.
     */
    public void fail(Workload workload) {
        try {
            submitInstruction(workload, ActionType.FAIL);
        }
        catch (ExecutionException e) {
            log.error("unhandled exception while failing workload " + workload.getUrn(), e);
        }
        catch (InterruptedException ignored) {
            log.error("interrupted while waiting for workload " + workload.getUrn() + " to fail");
        }
    }

    /**
     * Restarts the workload, regardless of which cluster member is running it.
     *
     * @param urn URN of the workload to restart.
     */
    public void restart(String urn) {
        Workload workload = getWorkload(urn);

        if (workload == null) {
            throw new IllegalArgumentException("could not locate a workload with the URN " + urn);
        }

        restart(workload);

    }

    /**
     * Restarts the workload, regardless of which cluster member is running it.
     *
     * @param workload Workload to restart.
     */
    public void restart(Workload workload) {
        try {
            submitInstruction(workload, ActionType.RESTART);
        }
        catch (ExecutionException e) {
            log.error("unhandled exception while restarting workload " + workload.getUrn(), e);
        }
        catch (InterruptedException ignored) {
            log.error("interrupted while waiting for workload " + workload.getUrn() + " to restart");
        }
    }

    /**
     * Attempts to locate a workload by its URN.
     *
     * @param urn URN of the workload
     * @return The workload with the given URN, or {@code null}.
     */
    public Workload getWorkload(String urn) {
        return workloadRepository.lookup(urn);
    }

    /**
     * Submits an instruction to the cluster.
     *
     * @param workload   Workload to execute the action against.
     * @param actionType Action to take on the workload.
     * @throws ExecutionException   When an error in execution occurs.
     * @throws InterruptedException When waiting for the execution to complete is interrupted.
     */
    private void submitInstruction(Workload workload, ActionType actionType) throws ExecutionException, InterruptedException {
        SchedulerAction action = new SchedulerAction(workload, actionType);
        WorkloadActionsInstruction instruction = new WorkloadActionsInstruction(Collections.singletonList(action));
        clusterManager.submitInstruction(instruction);
    }
}
