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

import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadRepositorySource;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * For demonstration purposes, simply returns the static list of workloads to run.
 * In non-trivial implementations, workload repository sources might load workloads
 * from a database or some other service.
 */
@Component
public class ExampleWorkloadRepositorySource implements WorkloadRepositorySource {
    private static final Set<Workload> workloads;

    static {
        workloads = new HashSet<>();
        workloads.add(new ExampleWorkload("a"));
        workloads.add(new ExampleWorkload("b"));
        workloads.add(new ExampleWorkload("c"));
    }

    @Override
    public Set<Workload> queryWorkloads() {
        return workloads;
    }
}
