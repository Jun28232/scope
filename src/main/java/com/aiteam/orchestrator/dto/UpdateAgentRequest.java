package com.aiteam.orchestrator.dto;

/**
 * 更新Agent请求DTO (Agent配置侧边栏)
 */
public class UpdateAgentRequest {
    private String roleName;
    private String description;
    private String agentType;
    private boolean active;

    // Getters and Setters
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}