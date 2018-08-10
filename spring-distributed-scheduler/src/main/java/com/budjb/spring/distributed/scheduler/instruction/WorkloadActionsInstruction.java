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

package com.budjb.spring.distributed.scheduler.instruction;

import com.budjb.spring.distributed.cluster.Instruction;
import com.budjb.spring.distributed.scheduler.strategy.SchedulerAction;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An instruction created by the scheduler system that conducts changes to workload
 * assignments for a specific cluster member.
 */
public class WorkloadActionsInstruction implements Instruction<Boolean> {
    /**
     * Scheduler actions to perform.
     */
    private final List<SchedulerAction> actions;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(WorkloadActionsInstruction.class);

    /**
     * Workload context manager.
     */
    private transient WorkloadContextManager workloadContextManager;

    /**
     * Constructor.
     *
     * @param actions the scheduler actions to perform.
     */
    public WorkloadActionsInstruction(List<SchedulerAction> actions) {
        this.actions = actions;
    }

    /**
     * Sets the workload context manager.
     *
     * @param workloadContextManager Workload context manager.
     */
    @Autowired
    public void setWorkloadContextManager(WorkloadContextManager workloadContextManager) {
        this.workloadContextManager = workloadContextManager;
    }

    /**
     * Returns the scheduler actions to perform.
     *
     * @return the scheduler actions to perform.
     */
    public List<SchedulerAction> getActions() {
        return actions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean call() {
        List<Future<?>> futures = new ArrayList<>();

        actions.removeIf(a -> {
            if (a.getActionType() == ActionType.ADD) {
                return false;
            }
            if (!workloadContextManager.isServicing(a.getWorkload())) {
                log.warn("Discarding " + a.getActionType() + " instruction for workload " + a.getWorkload() + " because the node is not servicing it");
            }
            return !workloadContextManager.isServicing(a.getWorkload());
        });

        for (SchedulerAction action : actions) {
            Workload workload = action.getWorkload();

            try {
                switch (action.getActionType()) {
                    case ADD:
                        log.info("Adding workload " + workload + " to node");
                        workloadContextManager.start(workload);
                        break;

                    case STOP:
                        log.info("Stopping workload " + workload + " on node");
                        futures.add(workloadContextManager.stop(workload));
                        break;

                    case REMOVE:
                        log.info("Removing workload " + workload + " from node");
                        futures.add(workloadContextManager.remove(workload));
                        break;

                    case RESTART:
                        log.info("Restarting workload " + workload + " on node");
                        futures.add(workloadContextManager.restart(workload));
                        break;

                    case FAIL:
                        log.info("Failing workload " + workload + " on node");
                        workloadContextManager.fail(workload);
                        break;

                    default:
                        throw new UnsupportedOperationException("action type " + action.getActionType().toString() + " is unsupported");
                }
            }
            catch (UnsupportedOperationException e) {
                throw e;
            }
            catch (Exception e) {
                log.error("Unhandled exception encountered while starting workload " + workload.toString(), e);
            }
        }

        try {
            while (futures.size() > 0) {
                futures.removeIf(Future::isDone);
                Thread.sleep(250);
            }
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            log.info("Interrupted while waiting for instructions to complete");
        }

        return true;
    }
}
