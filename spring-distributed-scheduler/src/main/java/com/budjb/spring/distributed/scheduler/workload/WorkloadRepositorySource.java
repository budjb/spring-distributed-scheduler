package com.budjb.spring.distributed.scheduler.workload;

import java.util.Set;

public interface WorkloadRepositorySource {
    Set<Workload> queryWorkloads();
}
