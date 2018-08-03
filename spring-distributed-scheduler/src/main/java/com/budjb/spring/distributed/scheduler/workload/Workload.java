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

import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Provides the basis for representing a a unit of work that can be considered a workload.
 */
public abstract class Workload implements Serializable {
    /**
     * ID of the workload.
     */
    private final String id;

    /**
     * URN of the workload.
     * <p>
     * The URN is expected to be unique per endpoint type.
     */
    private String urn;

    /**
     * Constructor.
     *
     * @param id ID of the workload.
     */
    protected Workload(String id) {
        Assert.notNull(id, "the [id] of a workload may not be null");
        this.id = id;
    }

    /**
     * Returns the URN of the workload.
     *
     * @return the URN of the workload.
     */
    public String getUrn() {
        if (urn == null) {
            synchronized (this) {
                if (urn == null) {
                    urn = "urn:workload:" + getType() + ":" + id;
                }
            }
        }
        return urn;
    }

    /**
     * The type of the endpoint.
     *
     * @return the endpoint type.
     */
    protected String getType() {
        return getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getUrn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        Workload endpoint = (Workload) o;

        return getUrn().equals(endpoint.getUrn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result;
        result = (getUrn() != null ? getUrn().hashCode() : 0);
        return result;
    }
}