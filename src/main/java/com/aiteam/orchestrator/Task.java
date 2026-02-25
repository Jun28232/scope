package com.aiteam.orchestrator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务实体类，表示一个可执行的任务单元
 */
public class Task {

    private final String id;                    // 任务唯一标识
    private final String role;                 // 执行角色（前端、后端、测试等）
    private final List<String> dependencies;   // 前置任务ID列表
    private TaskStatus status;               // 任务状态
    private int retryCount;                  // 重试次数
    private LocalDateTime createdAt;         // 创建时间
    private LocalDateTime updatedAt;         // 更新时间
    private String description;              // 任务描述

    public Task(String id, String role, List<String> dependencies, TaskStatus status,
                int retryCount, LocalDateTime createdAt, LocalDateTime updatedAt, String description) {
        this.id = id;
        this.role = role;
        this.dependencies = dependencies != null ? dependencies : java.util.Collections.emptyList();
        this.status = status;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
    }

    // Getters
    public String getId() { return id; }
    public String getRole() { return role; }
    public List<String> getDependencies() { return dependencies; }
    public TaskStatus getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getDescription() { return description; }

    public enum TaskStatus {
        PENDING,      // 等待执行
        RUNNING,      // 执行中
        COMPLETED,    // 已完成
        FAILED,       // 执行失败
        RETRYING      // 重试中
    }

    /**
     * 创建新任务
     */
    public static Task create(String id, String role, List<String> dependencies, String description) {
        return new Task(
            id,
            role,
            dependencies,
            TaskStatus.PENDING,
            0,
            LocalDateTime.now(),
            LocalDateTime.now(),
            description
        );
    }

    /**
     * 任务开始执行
     */
    public Task start() {
        return new Task(
            id, role, dependencies, TaskStatus.RUNNING, retryCount,
            createdAt, LocalDateTime.now(), description
        );
    }

    /**
     * 任务完成
     */
    public Task complete() {
        return new Task(
            id, role, dependencies, TaskStatus.COMPLETED, retryCount,
            createdAt, LocalDateTime.now(), description
        );
    }

    /**
     * 任务失败
     */
    public Task fail() {
        return new Task(
            id, role, dependencies, TaskStatus.FAILED, retryCount,
            createdAt, LocalDateTime.now(), description
        );
    }

    /**
     * 任务重试
     */
    public Task retry() {
        return new Task(
            id, role, dependencies, TaskStatus.RETRYING, retryCount + 1,
            createdAt, LocalDateTime.now(), description
        );
    }
}