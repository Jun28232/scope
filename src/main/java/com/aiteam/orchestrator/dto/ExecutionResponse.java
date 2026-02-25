package com.aiteam.orchestrator.dto;

/**
 * 项目执行响应DTO
 */
public class ExecutionResponse {
    private String projectId;
    private String status;
    private String message;

    public ExecutionResponse() {}

    public ExecutionResponse(String projectId, String status, String message) {
        this.projectId = projectId;
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}