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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * An implementation of {@link WorkloadContext} that provides a framework around
 * running a workload that will only ever use one {@link WorkloadRunnable} and thread.
 */
public class SingleThreadedWorkloadContext implements WorkloadContext {
    /**
     * Workload runnable.
     */
    private final WorkloadRunnable runnable;

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(SingleThreadedWorkloadContext.class);

    /**
     * Workload runnable thread.
     */
    private Thread thread;

    /**
     * Constructor.
     *
     * @param runnable Workload runnable.
     */
    public SingleThreadedWorkloadContext(WorkloadRunnable runnable) {
        Assert.notNull(runnable, "workload runnable must not be null");
        this.runnable = runnable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Workload getWorkload() {
        return runnable.getWorkload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadReport.Entry getWorkloadReportEntry() {
        return new WorkloadReport.Entry(getWorkload(), runnable.getRunningState(), runnable.getException() != null ? runnable.getException().getMessage() : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (thread != null) {
            throw new IllegalStateException("context for workload " + getWorkload().getUrn() + " has already previously been started");
        }

        thread = new Thread(runnable, "runnable-" + getWorkload().getUrn());
        thread.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (thread != null && thread.isAlive() && !thread.isInterrupted()) {
            thread.interrupt();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStopped() {
        if (thread == null) {
            return true;
        }

        if (!thread.isAlive()) {
            thread = null;

            if (!runnable.getRunningState().isTerminated()) {
                runnable.terminate();
            }

            return true;
        }

        return runnable.getRunningState().isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate() {
        if (thread == null) {
            return;
        }

        try {
            thread.interrupt();
            thread.join();
        }
        catch (InterruptedException e) {
            log.error("unable to successfully terminate a workload thread due to being interrupted", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fail() {
        runnable.fail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RunningState getRunningState() {
        return runnable.getRunningState();
    }

    /**
     * Sets the priority of the thread.
     *
     * @param priority New priority of the thread.
     */
    public void setThreadPriority(int priority) {
        if (thread != null) {
            thread.setPriority(priority);
        }
    }
}
