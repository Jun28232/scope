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
    private final AgentRepository agentRepository;

    public AgentOrchestrator(ProjectStateRepository stateRepository, AgentRepository agentRepository) {
        this.stateRepository = stateRepository;
        this.agentRepository = agentRepository;
        this.agents = new ConcurrentHashMap<>();
        initializeAgents();
    }

    /**
     * 初始化所有角色代理
     */
    private void initializeAgents() {
        // 从数据库加载所有可用的代理
        List<Agent> availableAgents = agentRepository.findAll();
        for (Agent agent : availableAgents) {
            agents.put(agent.getRoleName(), agent.getAgentInstance());
        }
    }

    /**
     * 执行项目计划
     */
    public void executeProject(ProjectPlan projectPlan) {
        ProjectState state = new ProjectState(projectPlan.getProjectId());
        projectPlan.getTasks().forEach(state::addTask);

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
            List<Task> executableTasks = projectPlan.getTasks().stream()
                .filter(task -> state.canExecuteTask(task.getId()))
                .filter(task -> state.getTaskStatus(task.getId()) == Task.TaskStatus.PENDING)
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
            state.updateTaskStatus(task.getId(), Task.TaskStatus.RUNNING);

            // 获取对应的代理
            RoleBasedAgent agent = agents.get(task.getRole());
            if (agent == null) {
                throw new OrchestratorException("Unknown agent role: " + task.getRole());
            }

            // 执行任务
            boolean success = agent.execute(task);

            if (success) {
                state.updateTask(task.complete());
                System.out.println("Task completed: " + task.getId() + " (" + task.getRole() + ")");
                return true;
            } else {
                throw new AgentExecutionException("Agent execution failed: " + task.getId());
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
        int currentRetryCount = state.getRetryCount(task.getId());
        int maxRetries = 3; // 最大重试次数

        if (currentRetryCount < maxRetries) {
            // 重试任务
            state.incrementRetryCount(task.getId());
            Task retriedTask = task.retry();
            state.updateTask(retriedTask);
            System.out.println("Task retrying: " + task.getId() + " (attempt " + (currentRetryCount + 1) + ")");
        } else {
            // 超过最大重试次数，标记为失败
            state.updateTask(task.fail());
            state.updateTaskStatus(task.getId(), Task.TaskStatus.FAILED);
            System.out.println("Task failed after max retries: " + task.getId());
        }
    }

    /**
     * 尝试重试失败的任务
     */
    private boolean tryRetryFailedTasks(ProjectPlan projectPlan, ProjectState state) {
        boolean hasChanges = false;

        List<Task> failedTasks = projectPlan.getTasks().stream()
            .filter(task -> state.getTaskStatus(task.getId()) == Task.TaskStatus.FAILED)
            .collect(Collectors.toList());

        for (Task failedTask : failedTasks) {
            if (state.getRetryCount(failedTask.getId()) < 3) {
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
            state.getTaskMap().values().stream().collect(Collectors.toList()),
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