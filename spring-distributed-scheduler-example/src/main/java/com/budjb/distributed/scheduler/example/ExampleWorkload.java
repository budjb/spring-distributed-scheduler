package com.budjb.distributed.scheduler.example;

import com.budjb.spring.distributed.scheduler.workload.Workload;

/**
 * This trivial example of a workload does not need anything more than to simply exist.
 * Non-trivial implementations may need additional information, such as the URL
 * of service to work against or some other data specific to the workload to
 * process.
 */
public class ExampleWorkload extends Workload {
    /**
     * Constructor.
     *
     * @param id ID of the workload.
     */
    public ExampleWorkload(String id) {
        super(id);
    }
}
