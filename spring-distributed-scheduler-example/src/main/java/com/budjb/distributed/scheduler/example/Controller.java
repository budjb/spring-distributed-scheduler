package com.budjb.distributed.scheduler.example;

import com.budjb.spring.distributed.scheduler.cluster.ClusterManager;
import com.budjb.spring.distributed.scheduler.instruction.ReportInstruction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Controller {
    private final ClusterManager clusterManager;

    Controller(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @RequestMapping(path="/", method = RequestMethod.GET)
    Map status() {
        try {
            return clusterManager.submitInstruction(new ReportInstruction());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
