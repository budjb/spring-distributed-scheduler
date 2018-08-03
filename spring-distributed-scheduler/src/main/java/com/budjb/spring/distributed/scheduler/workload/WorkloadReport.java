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

import com.budjb.spring.distributed.scheduler.RunningState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An aggregation of workload reports for a single cluster member.
 */
public class WorkloadReport implements Serializable, Cloneable {
    /**
     * List of workloads that are being collected along with their status.
     */
    private final List<Entry> entries;

    /**
     * Constructor.
     */
    public WorkloadReport() {
        this(new ArrayList<>());
    }

    /**
     * Constructor.
     *
     * @param entries individual workload reports.
     */
    public WorkloadReport(List<Entry> entries) {
        this.entries = entries;
    }

    /**
     * Returns the list of workload entries.
     *
     * @return the list of workload entries.
     */
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * Adds a collector report entry.
     *
     * @param entry an individual workload report.
     */
    public void add(Entry entry) {
        entries.add(entry);
    }

    /**
     * Returns a deep copy of the report.
     *
     * @return a deep copy of the report.
     */
    public WorkloadReport copy() {
        return new WorkloadReport(entries.stream().map(entry -> new Entry(entry.getWorkload(), entry.getState(), entry.getError())).collect(Collectors.toList()));
    }

    /**
     * Workload report entry.
     */
    public static class Entry implements Serializable, Cloneable {
        /**
         * Workload.
         */
        private final Workload workload;

        /**
         * Running state.
         */
        private final RunningState state;

        /**
         * Error (may be null).
         */
        private final String error;

        /**
         * Constructor.
         *
         * @param workload workload of the entry.
         * @param state    running state of the workload.
         */
        public Entry(Workload workload, RunningState state) {
            this(workload, state, (String) null);
        }

        /**
         * Constructor.
         *
         * @param workload  workload of the entry.
         * @param state     running state of the workload.
         * @param exception the exception that was thrown while processing the workload.
         */
        public Entry(Workload workload, RunningState state, Exception exception) {
            this(workload, state, exception != null ? exception.getMessage() : null);
        }

        /**
         * Constructor.
         *
         * @param workload workload of the entry.
         * @param state    running state of the workload.
         * @param error    error message that occurred (may be {code}null{code}).
         */
        public Entry(Workload workload, RunningState state, String error) {
            this.workload = workload;
            this.state = state;
            this.error = error;
        }

        /**
         * Returns the workload of the entry.
         *
         * @return the workload of the entry.
         */
        public Workload getWorkload() {
            return workload;
        }

        /**
         * Returns the running state of the entry.
         *
         * @return the running state of the entry.
         */
        public RunningState getState() {
            return state;
        }

        /**
         * Returns the error throw while processing the workload.
         *
         * @return the error throw while processing the workload.
         */
        public String getError() {
            return error;
        }
    }
}
