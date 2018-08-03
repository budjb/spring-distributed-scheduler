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
