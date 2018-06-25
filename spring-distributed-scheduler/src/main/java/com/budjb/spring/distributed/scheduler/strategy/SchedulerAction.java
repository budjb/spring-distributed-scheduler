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

package com.budjb.spring.distributed.scheduler.strategy;

import com.budjb.spring.distributed.scheduler.instruction.ActionType;
import com.budjb.spring.distributed.scheduler.workload.Workload;

import java.io.Serializable;

/**
 * Contains an individual action to perform on a specific workload.
 */
public class SchedulerAction implements Serializable {
    /**
     * The workload to act on.
     */
    private final Workload workload;

    /**
     * Action to perform.
     */
    private final ActionType actionType;

    /**
     * Constructor.
     *
     * @param workload   workload the action should be taken on.
     * @param actionType action to take.
     */
    public SchedulerAction(Workload workload, ActionType actionType) {
        this.workload = workload;
        this.actionType = actionType;
    }

    /**
     * Returns the workload to perform the action on.
     *
     * @return the workload to perform the action on.
     */
    public Workload getWorkload() {
        return workload;
    }

    /**
     * Returns the action to perform.
     *
     * @return the action to perform.
     */
    public ActionType getActionType() {
        return actionType;
    }
}
