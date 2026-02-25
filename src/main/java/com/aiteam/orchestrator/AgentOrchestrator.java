package com.aiteam.orchestrator;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Agent编排器，负责任务调度和执行协调
 */
@Service
public class AgentOrchestrator {

    private final Map<String, RoleBasedAgent> agents;
    private final ProjectStateRepository stateRepository;

    public AgentOrchestrator(ProjectStateRepository stateRepository) {
        this.stateRepository = stateRepository;
        this.agents = new ConcurrentHashMap<>();
        initializeAgents();
    }

    /**
     * 初始化所有角色代理
     */
    private void initializeAgents() {
        agents.put("后端", new BackendAgent());
        agents.put("前端", new FrontendAgent());
        agents.put("测试", new TestingAgent());
        agents.put("架构设计", new ArchitectureAgent());
    }

    /**
     * 执行项目计划
     */
    public void executeProject(ProjectPlan projectPlan) {
        ProjectState state = new ProjectState(projectPlan.projectId());
        projectPlan.tasks().forEach(state::addTask);

        // 保存初始状态
        state.saveToDatabase();

        try {
            executeTasks(projectPlan, state);
        } catch (Exception e) {
            throw new OrchestratorException("Project execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * 执行任务列表
     */
    private void executeTasks(ProjectPlan projectPlan, ProjectState state) {
        boolean hasChanges = true;

        while (hasChanges && !state.isProjectCompleted() && !state.hasFailedTasks()) {
            hasChanges = false;

            // 获取所有待执行的任务
            List<Task> executableTasks = projectPlan.tasks().stream()
                .filter(task -> state.canExecuteTask(task.id()))
                .filter(task -> state.getTaskStatus(task.id()) == Task.TaskStatus.PENDING)
                .collect(Collectors.toList());

            // 执行可执行的任务
            for (Task task : executableTasks) {
                if (executeTask(task, state)) {
                    hasChanges = true;
                }
            }

            // 如果有失败的任务，尝试重试
            if (!state.hasFailedTasks()) {
                hasChanges = tryRetryFailedTasks(projectPlan, state);
            }

            // 保存状态
            if (hasChanges) {
                state.saveToDatabase();
            }
        }
    }

    /**
     * 执行单个任务
     */
    private boolean executeTask(Task task, ProjectState state) {
        try {
            state.updateTaskStatus(task.id(), Task.TaskStatus.RUNNING);

            // 获取对应的代理
            RoleBasedAgent agent = agents.get(task.role());
            if (agent == null) {
                throw new OrchestratorException("Unknown agent role: " + task.role());
            }

            // 执行任务
            boolean success = agent.execute(task);

            if (success) {
                state.updateTask(task.complete());
                System.out.println("Task completed: " + task.id() + " (" + task.role() + ")");
                return true;
            } else {
                throw new AgentExecutionException("Agent execution failed: " + task.id());
            }

        } catch (AgentExecutionException e) {
            handleTaskFailure(task, state);
            return false;
        } catch (Exception e) {
            handleTaskFailure(task, state);
            return false;
        }
    }

    /**
     * 处理任务失败
     */
    private void handleTaskFailure(Task task, ProjectState state) {
        int currentRetryCount = state.getRetryCount(task.id());
        int maxRetries = 3; // 最大重试次数

        if (currentRetryCount < maxRetries) {
            // 重试任务
            state.incrementRetryCount(task.id());
            Task retriedTask = task.retry();
            state.updateTask(retriedTask);
            System.out.println("Task retrying: " + task.id() + " (attempt " + (currentRetryCount + 1) + ")");
        } else {
            // 超过最大重试次数，标记为失败
            state.updateTask(task.fail());
            state.updateTaskStatus(task.id(), Task.TaskStatus.FAILED);
            System.out.println("Task failed after max retries: " + task.id());
        }
    }

    /**
     * 尝试重试失败的任务
     */
    private boolean tryRetryFailedTasks(ProjectPlan projectPlan, ProjectState state) {
        boolean hasChanges = false;

        List<Task> failedTasks = projectPlan.tasks().stream()
            .filter(task -> state.getTaskStatus(task.id()) == Task.TaskStatus.FAILED)
            .collect(Collectors.toList());

        for (Task failedTask : failedTasks) {
            if (state.getRetryCount(failedTask.id()) < 3) {
                if (executeTask(failedTask, state)) {
                    hasChanges = true;
                }
            }
        }

        return hasChanges;
    }

    /**
     * 断点续传 - 从指定状态继续执行
     */
    public void resumeExecution(String projectId) {
        ProjectState state = stateRepository.load(projectId);
        if (state == null) {
            throw new OrchestratorException("Project state not found: " + projectId);
        }

        ProjectPlan projectPlan = new ProjectPlan(
            state.getProjectId(),
            "Resumed Project",
            "Resumed execution",
            state.getTaskMap().values().stream().toList(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        executeTasks(projectPlan, state);
    }

    /**
     * 获取项目执行状态
     */
    public ProjectState getProjectState(String projectId) {
        return stateRepository.load(projectId);
    }

    // 自定义异常类
    public static class OrchestratorException extends RuntimeException {
        public OrchestratorException(String message, Throwable cause) {
            super(message, cause);
        }

        public OrchestratorException(String message) {
            super(message);
        }
    }

    public static class AgentExecutionException extends RuntimeException {
        public AgentExecutionException(String message) {
            super(message);
        }
    }
}