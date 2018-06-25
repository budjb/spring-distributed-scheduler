package com.budjb.spring.distributed.scheduler.support.cluster

import com.budjb.spring.distributed.cluster.Instruction

import java.util.concurrent.Future

class TestInstruction implements Instruction<Void> {
    Future future

    @Override
    Void call() throws Exception {
        return null
    }
}
