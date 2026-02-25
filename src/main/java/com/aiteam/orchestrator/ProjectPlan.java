package com.aiteam.orchestrator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目计划类，包含任务列表和项目元数据
 */
public class ProjectPlan {

    private final String projectId;           // 项目ID
    private final String title;              // 项目标题
    private final String description;        // 项目描述
    private final List<Task> tasks;          // 任务列表
    private final LocalDateTime createdAt;   // 创建时间
    private final LocalDateTime updatedAt;   // 更新时间

    public ProjectPlan(String projectId, String title, String description, List<Task> tasks,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.tasks = tasks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Task> getTasks() { return tasks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /**
     * 根据角色获取任务列表
     */
    public List<Task> getTasksByRole(String role) {
        return tasks.stream()
            .filter(task -> task.getRole().equals(role))
            .collect(Collectors.toList());
    }

    /**
     * 获取所有前置依赖关系
     */
    public Map<String, List<String>> getDependencies() {
        return tasks.stream()
            .collect(Collectors.toMap(
                Task::getId,
                Task::getDependencies
            ));
    }

    /**
     * 获取所有依赖当前任务的任务
     */
    public Map<String, List<String>> getDependents() {
        return tasks.stream()
            .flatMap(task -> task.getDependencies().stream()
                .map(depId -> java.util.AbstractMap.SimpleImmutableEntry.of(depId, task.getId())))
            .collect(Collectors.groupingBy(
                java.util.AbstractMap.SimpleImmutableEntry::getKey,
                Collectors.mapping(java.util.AbstractMap.SimpleImmutableEntry::getValue, Collectors.toList())
            ));
    }

    /**
     * 创建新项目计划
     */
    public static ProjectPlan create(String projectId, String title, String description, List<Task> tasks) {
        return new ProjectPlan(
            projectId,
            title,
            description,
            tasks,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    /**
     * 更新项目计划
     */
    public ProjectPlan update(List<Task> updatedTasks) {
        return new ProjectPlan(
            projectId, title, description, updatedTasks,
            createdAt, LocalDateTime.now()
        );
    }
}