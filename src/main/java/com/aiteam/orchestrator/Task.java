package com.aiteam.orchestrator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务实体类，表示一个可执行的任务单元
 */
public record Task(
    String id,                    // 任务唯一标识
    String role,                 // 执行角色（前端、后端、测试等）
    List<String> dependencies,   // 前置任务ID列表
    TaskStatus status,           // 任务状态
    int retryCount,             // 重试次数
    LocalDateTime createdAt,     // 创建时间
    LocalDateTime updatedAt,     // 更新时间
    String description          // 任务描述
) {

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
            dependencies != null ? dependencies : List.of(),
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