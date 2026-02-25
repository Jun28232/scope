package com.aiteam.orchestrator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目计划类，包含任务列表和项目元数据
 */
public record ProjectPlan(
    String projectId,           // 项目ID
    String title,              // 项目标题
    String description,        // 项目描述
    List<Task> tasks,          // 任务列表
    LocalDateTime createdAt,   // 创建时间
    LocalDateTime updatedAt    // 更新时间
) {

    /**
     * 根据角色获取任务列表
     */
    public List<Task> getTasksByRole(String role) {
        return tasks.stream()
            .filter(task -> task.role().equals(role))
            .collect(Collectors.toList());
    }

    /**
     * 获取所有前置依赖关系
     */
    public Map<String, List<String>> getDependencies() {
        return tasks.stream()
            .collect(Collectors.toMap(
                Task::id,
                Task::dependencies
            ));
    }

    /**
     * 获取所有依赖当前任务的任务
     */
    public Map<String, List<String>> getDependents() {
        return tasks.stream()
            .flatMap(task -> task.dependencies().stream()
                .map(depId -> Map.entry(depId, task.id())))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
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