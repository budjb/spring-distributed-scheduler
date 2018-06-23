package com.budjb.distributed.scheduler.example;

import com.budjb.spring.distributed.scheduler.RunningState;
import com.budjb.spring.distributed.scheduler.workload.AbstractWorkloadRunnable;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleWorkloadRunnable extends AbstractWorkloadRunnable {
    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ExampleWorkloadRunnable.class);

    /**
     * Constructor.
     *
     * @param workload Workload that this runnable services.
     */
    public ExampleWorkloadRunnable(Workload workload) {
        super(workload);
    }

    @Override
    public void run() {
        setRunningState(RunningState.RUNNING);

        try {
            while (!isInterrupted()) {
                log.info("Workload " + getWorkload().getUrn() + " ticked.");
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ignored) {
                    setRunningState(RunningState.STOPPED);
                }
            }

            setRunningState(RunningState.STOPPED);
        }
        catch (Exception e) {
            setRunningState(RunningState.ERROR);
        }
    }
}
