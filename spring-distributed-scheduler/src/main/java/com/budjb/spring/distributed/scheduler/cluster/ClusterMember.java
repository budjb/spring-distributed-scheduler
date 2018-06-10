package com.budjb.spring.distributed.scheduler.cluster;

import org.springframework.util.Assert;

/**
 * Provides a basis for representing required information about a cluster member.
 */
public abstract class ClusterMember {
    /**
     * ID of the cluster member.
     */
    private final String id;

    /**
     * URN of the cluster member.
     */
    private String urn;

    /**
     * Constructor.
     *
     * @param id ID of the member.
     */
    public ClusterMember(String id) {
        Assert.notNull(id, "the [id] of a cluster member may not be null");
        this.id = id;
    }

    /**
     * Returns the cluster member's URN.
     *
     * @return the cluster member's URN.
     */
    public String getUrn() {
        if (urn == null) {
            synchronized (this) {
                if (urn == null) {
                    urn = "urn:cluster-member:" + getType() + ":" + id;
                }
            }
        }
        return urn;
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

        ClusterMember that = (ClusterMember) o;

        return getUrn().equals(that.getUrn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (getUrn() != null ? getUrn().hashCode() : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return getUrn();
    }

    /**
     * Returns the type of the cluster member. The type is used as part of a cluster
     * member's URN string.
     *
     * @return the type of the cluster member.
     */
    protected String getType() {
        return getClass().getSimpleName();
    }
}
