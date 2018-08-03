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

package com.budjb.distributed.scheduler.example;

import com.budjb.spring.distributed.cluster.ClusterManager;
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
