package com.aiteam.orchestrator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目状态类，维护全局上下文和持久化状态
 */
public class ProjectState {

    private final String projectId;
    private final Map<String, Task> taskMap;           // 任务ID -> 任务
    private final Map<String, Task.TaskStatus> statusMap;   // 快速状态查询
    private final Map<String, Integer> retryCountMap; // 重试计数
    private LocalDateTime lastUpdated;
    private ProjectStatus overallStatus;

    public ProjectState(String projectId) {
        this.projectId = projectId;
        this.taskMap = new ConcurrentHashMap<>();
        this.statusMap = new ConcurrentHashMap<>();
        this.retryCountMap = new ConcurrentHashMap<>();
        this.lastUpdated = LocalDateTime.now();
        this.overallStatus = ProjectStatus.PENDING;
    }

    // Getters
    public String getProjectId() { return projectId; }
    public Map<String, Task> getTaskMap() { return new ConcurrentHashMap<>(taskMap); }
    public Map<String, Task.TaskStatus> getStatusMap() { return new ConcurrentHashMap<>(statusMap); }
    public Map<String, Integer> getRetryCountMap() { return new ConcurrentHashMap<>(retryCountMap); }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public ProjectStatus getOverallStatus() { return overallStatus; }

    // Task management
    public void addTask(Task task) {
        taskMap.put(task.getId(), task);
        statusMap.put(task.getId(), task.getStatus());
        retryCountMap.put(task.getId(), task.getRetryCount());
        updateOverallStatus();
    }

    public void updateTask(Task updatedTask) {
        taskMap.put(updatedTask.getId(), updatedTask);
        statusMap.put(updatedTask.getId(), updatedTask.getStatus());
        retryCountMap.put(updatedTask.getId(), updatedTask.getRetryCount());
        updateOverallStatus();
        lastUpdated = LocalDateTime.now();
    }

    public Task getTask(String taskId) {
        return taskMap.get(taskId);
    }

    public boolean hasTask(String taskId) {
        return taskMap.containsKey(taskId);
    }

    // Status management
    public void updateTaskStatus(String taskId, Task.TaskStatus status) {
        statusMap.put(taskId, status);
        updateOverallStatus();
    }

    public Task.TaskStatus getTaskStatus(String taskId) {
        return statusMap.getOrDefault(taskId, Task.TaskStatus.PENDING);
    }

    // Retry management
    public void incrementRetryCount(String taskId) {
        int current = retryCountMap.getOrDefault(taskId, 0);
        retryCountMap.put(taskId, current + 1);
    }

    public int getRetryCount(String taskId) {
        return retryCountMap.getOrDefault(taskId, 0);
    }

    // Completion checks
    public boolean isTaskCompleted(String taskId) {
        return getTaskStatus(taskId) == Task.TaskStatus.COMPLETED;
    }

    public boolean isProjectCompleted() {
        return overallStatus == ProjectStatus.COMPLETED;
    }

    public boolean hasFailedTasks() {
        return statusMap.values().stream()
            .anyMatch(status -> status == Task.TaskStatus.FAILED);
    }

    // Dependency checks
    public boolean canExecuteTask(String taskId) {
        Task task = getTask(taskId);
        if (task == null) return false;

        // 检查所有前置任务是否已完成
        return task.getDependencies().stream()
            .allMatch(depId -> {
                Task depTask = getTask(depId);
                return depTask == null || isTaskCompleted(depId);
            });
    }

    // Persistence simulation
    public void saveToDatabase() {
        // TODO: 实现数据库持久化逻辑
        lastUpdated = LocalDateTime.now();
        System.out.println("ProjectState saved to database at " + lastUpdated);
    }

    public void loadFromDatabase() {
        // TODO: 实现从数据库加载逻辑
        System.out.println("ProjectState loaded from database");
    }

    private void updateOverallStatus() {
        if (taskMap.isEmpty()) {
            overallStatus = ProjectStatus.PENDING;
            return;
        }

        boolean allCompleted = taskMap.values().stream()
            .allMatch(task -> task.getStatus() == Task.TaskStatus.COMPLETED);

        boolean anyFailed = taskMap.values().stream()
            .anyMatch(task -> task.getStatus() == Task.TaskStatus.FAILED);

        if (allCompleted) {
            overallStatus = ProjectStatus.COMPLETED;
        } else if (anyFailed) {
            overallStatus = ProjectStatus.FAILED;
        } else {
            overallStatus = ProjectStatus.RUNNING;
        }
    }

    public enum ProjectStatus {
        PENDING,   // 等待开始
        RUNNING,   // 执行中
        COMPLETED, // 已完成
        FAILED     // 执行失败
    }
}