package com.aiteam.orchestrator.dto;

import java.time.LocalDateTime;

/**
 * 项目创建响应DTO
 */
public class ProjectResponse {
    private String status;
    private String message;
    private String projectId;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    public ProjectResponse() {}

    public ProjectResponse(String status, String message, String projectId, String title) {
        this.status = status;
        this.message = message;
        this.projectId = projectId;
        this.title = title;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}