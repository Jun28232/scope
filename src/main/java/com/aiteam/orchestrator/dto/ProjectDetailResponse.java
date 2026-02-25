package com.aiteam.orchestrator.dto;

import com.aiteam.orchestrator.Task;
import com.aiteam.orchestrator.ProjectState;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目详情响应DTO (对应看板视图)
 */
public class ProjectDetailResponse {
    private String projectId;
    private String status;
    private LocalDateTime lastUpdated;
    private List<KanbanColumn> kanbanColumns;
    private List<Task> tasks;

    // Getters and Setters
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public List<KanbanColumn> getKanbanColumns() { return kanbanColumns; }
    public void setKanbanColumns(List<KanbanColumn> kanbanColumns) { this.kanbanColumns = kanbanColumns; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    /**
     * 看板列DTO (对应设计图的5列布局)
     */
    public static class KanbanColumn {
        private String title;
        private String status;
        private List<Task> tasks;

        public KanbanColumn(String title, String status, List<Task> tasks) {
            this.title = title;
            this.status = status;
            this.tasks = tasks;
        }

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public List<Task> getTasks() { return tasks; }
        public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    }
}