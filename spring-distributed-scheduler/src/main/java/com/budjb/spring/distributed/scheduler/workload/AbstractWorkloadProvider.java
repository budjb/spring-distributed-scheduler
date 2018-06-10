package com.budjb.spring.distributed.scheduler.workload;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A base implementation of a {@link WorkloadProvider}.
 */
public abstract class AbstractWorkloadProvider implements WorkloadProvider {
    private Set<WorkloadContext> contexts = new HashSet<>();

    /**
     * Creates a {@link WorkloadContext} for the given workload.
     * <p>
     * The workload is guaranteed to not already be registered.
     *
     * @param workload Workload to create a context for.
     * @return A new context for the given workload.
     */
    protected abstract WorkloadContext createContext(Workload workload);

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadContext registerWorkload(Workload workload) {
        if (getContext(workload) != null) {
            throw new IllegalArgumentException("workload " + workload.toString() + " is already managed");
        }

        WorkloadContext context = createContext(workload);
        contexts.add(context);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WorkloadContext> getContexts() {
        return contexts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadContext getContext(Workload workload) {
        return contexts.stream().filter(w -> w.getWorkload().equals(workload)).findFirst().orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkloadContext removeContext(Workload workload) {
        Iterator<WorkloadContext> iterator = contexts.iterator();

        while (iterator.hasNext()) {
            WorkloadContext context = iterator.next();

            if (context.getWorkload().equals(workload)) {
                iterator.remove();
                return context;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WorkloadContext> removeAll() {
        Set<WorkloadContext> contexts = new HashSet<>(this.contexts);
        this.contexts.clear();
        return contexts;
    }
}
