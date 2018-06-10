package com.budjb.spring.distributed.scheduler.cluster

import com.budjb.spring.distributed.scheduler.support.cluster.Test2ClusterMember
import com.budjb.spring.distributed.scheduler.support.cluster.TestClusterMember
import spock.lang.Specification

class ClusterMemberSpec extends Specification {
    def 'When 2 different cluster member objects are the same class with the same ID, they are equal and their hashes match'() {
        setup:
        ClusterMember a = new TestClusterMember('1234')
        ClusterMember b = new TestClusterMember('1234')

        expect:
        !a.is(b)
        a.equals(b)
        b.equals(a)
        a.hashCode() == b.hashCode()
    }

    def 'getId() returns the ID the cluster member was constructed with'() {
        setup:
        ClusterMember clusterMember = new TestClusterMember('1234')

        expect:
        clusterMember.getUrn() == 'urn:cluster-member:TestClusterMember:1234'
    }

    def 'When 2 objects are actually the same object, they are considered equal and their hashcodes match'() {
        setup:
        ClusterMember a = new TestClusterMember('1234')
        ClusterMember b = a

        expect:
        a.is(b)
        a.equals(b)
        b.equals(a)
        a.hashCode() == b.hashCode()
    }

    def 'When 2 objects have the same ID but are not the same class type, they are not equal'() {
        setup:
        ClusterMember a = new TestClusterMember('1234')
        ClusterMember b = new Test2ClusterMember('1234')

        expect:
        !a.is(b)
        !a.equals(b)
        !b.equals(a)
    }
}
