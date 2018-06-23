package com.budjb.spring.distributed.scheduler.strategy;

import com.budjb.spring.distributed.scheduler.RunningState;
import com.budjb.spring.distributed.scheduler.cluster.ClusterMember;
import com.budjb.spring.distributed.scheduler.instruction.ActionType;
import com.budjb.spring.distributed.scheduler.instruction.SchedulerInstruction;
import com.budjb.spring.distributed.scheduler.workload.Workload;
import com.budjb.spring.distributed.scheduler.workload.WorkloadReport;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A base implementation of {@link SchedulerStrategy} that primarily provides a set of
 * helper methods for manipulating workload assignments between cluster members.
 */
public abstract class AbstractSchedulerStrategy implements SchedulerStrategy {
    /**
     * Removes the given workload regardless of what cluster member it's on.
     *
     * @param context  Scheduler strategy context.
     * @param workload Workload of the action.
     */
    protected void removeWorkload(SchedulerStrategyContext context, Workload workload) {
        for (Map.Entry<? extends ClusterMember, WorkloadReport> reportEntry : context.getMapping().entrySet()) {
            Iterator<WorkloadReport.Entry> iterator = reportEntry.getValue().getEntries().iterator();

            while (iterator.hasNext()) {
                WorkloadReport.Entry entry = iterator.next();

                if (entry.getWorkload().equals(workload)) {
                    iterator.remove();
                    addAction(context, reportEntry.getKey(), entry.getWorkload(), ActionType.REMOVE);
                }
            }
        }
    }

    /**
     * Restarts the given workload on the given cluster member.
     *
     * @param context       Scheduler strategy context.
     * @param clusterMember Cluster member to add the action to.
     * @param workload      Workload of the action.
     */
    protected void restartWorkload(SchedulerStrategyContext context, ClusterMember clusterMember, Workload workload) {
        addAction(context, clusterMember, workload, ActionType.RESTART);
    }

    /**
     * Removes the given workload from the given cluster member.
     *
     * @param context       Scheduler strategy context.
     * @param clusterMember Cluster member to add the action to.
     * @param workload      Workload of the action.
     */
    protected void removeWorkload(SchedulerStrategyContext context, ClusterMember clusterMember, Workload workload) {
        context.getMapping().get(clusterMember).getEntries().removeIf(e -> e.getWorkload().equals(workload));
        addAction(context, clusterMember, workload, ActionType.REMOVE);
    }

    /**
     * Adds the given workload from the given cluster member.
     *
     * @param context       Scheduler strategy context.
     * @param clusterMember Cluster member to add the action to.
     * @param workload      Workload of the action.
     */
    protected void addWorkload(SchedulerStrategyContext context, ClusterMember clusterMember, Workload workload) {
        context.getMapping().get(clusterMember).getEntries().add(new WorkloadReport.Entry(workload, RunningState.NOT_STARTED));
        addAction(context, clusterMember, workload, ActionType.ADD);
    }

    /**
     * Makes a deep copy of the given map of workload reports.
     *
     * @param reports Mapping of cluster members to workload reports.
     * @return a deep copy of the given map of workload reports.
     */
    protected Map<? extends ClusterMember, WorkloadReport> copyReports(Map<? extends ClusterMember, WorkloadReport> reports) {
        return reports.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().copy()));
    }

    /**
     * Adds a workload action to a cluster member.
     *
     * @param context       Scheduler strategy context.
     * @param clusterMember Cluster member to add the action to.
     * @param workload      Workload of the action.
     * @param actionType    Action to perform.
     */
    protected void addAction(SchedulerStrategyContext context, ClusterMember clusterMember, Workload workload, ActionType actionType) {
        addAction(context, clusterMember, new SchedulerAction(workload, actionType));
    }

    /**
     * Adds a workload action to a cluster member.
     *
     * @param context       Scheduler strategy context.
     * @param clusterMember Cluster member to add the action to.
     * @param action        Workload action to perform.
     */
    protected void addAction(SchedulerStrategyContext context, ClusterMember clusterMember, SchedulerAction action) {
        if (!context.getActions().containsKey(clusterMember)) {
            context.getActions().put(clusterMember, new ArrayList<>());
        }

        context.getActions().get(clusterMember).add(action);
    }

    /**
     * Returns a collection of instruction mappings resulting from a scheduling/re-balancing operation.
     * <p>
     * This method compiles instructions into two sets: first removals, and then start/restart. This
     * prevents race conditions where more than one cluster member is executing a workload.
     *
     * @param context Scheduler strategy context.
     * @return a collection of instructing  mappings suitable for submission to cluster members.
     */
    protected List<Map<ClusterMember, SchedulerInstruction>> toInstructionMap(SchedulerStrategyContext context) {
        List<Map<ClusterMember, SchedulerInstruction>> compiled = new ArrayList<>();

        Map<ClusterMember, SchedulerInstruction> instructions = compileInstructions(context, ActionType.REMOVE);

        if (instructions.size() > 0) {
            compiled.add(instructions);
        }

        instructions = compileInstructions(context, ActionType.ADD, ActionType.RESTART);

        if (instructions.size() > 0) {
            compiled.add(instructions);
        }

        return compiled;
    }

    /**
     * Returns a mapping of cluster members to action instructions based on the given operation types.
     *
     * @param context Scheduler strategy context.
     * @param filter  The type of actions to include in the instruction set.
     * @return a mapping of cluster members to action instructions based on the given operation types.
     */
    private Map<ClusterMember, SchedulerInstruction> compileInstructions(SchedulerStrategyContext context, ActionType... filter) {
        List<ActionType> filterList = Arrays.asList(filter);

        Map<ClusterMember, SchedulerInstruction> instructions = new HashMap<>();

        for (ClusterMember clusterMember : context.getActions().keySet()) {
            List<SchedulerAction> schedulerActions = context.getActions().get(clusterMember);

            if (schedulerActions == null || schedulerActions.size() == 0) {
                continue;
            }

            List<SchedulerAction> actionsList = new ArrayList<>();

            for (SchedulerAction schedulerAction : schedulerActions) {
                if (filterList.contains(schedulerAction.getActionType())) {
                    actionsList.add(schedulerAction);
                }
            }

            if (actionsList.size() > 0) {
                instructions.put(clusterMember, new SchedulerInstruction(actionsList));
            }
        }

        return instructions;
    }

    static class EntryRunningStateComparator implements Comparator<WorkloadReport.Entry> {
        @Override
        public int compare(WorkloadReport.Entry o1, WorkloadReport.Entry o2) {
            if (o1.getState().isTerminated() && o2.getState().isTerminated()) {
                return 0;
            }
            else if (o1.getState().isTerminated()) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }
}
