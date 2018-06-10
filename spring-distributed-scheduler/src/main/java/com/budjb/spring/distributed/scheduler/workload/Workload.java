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