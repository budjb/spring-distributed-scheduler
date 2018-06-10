package com.budjb.spring.distributed.scheduler.support.workload

import com.budjb.spring.distributed.scheduler.workload.Workload

class TestWorkload extends Workload {
    TestWorkload(String id) {
        super(id)
    }
}

class Test2Workload extends TestWorkload {
    Test2Workload(String id) {
        super(id)
    }
}