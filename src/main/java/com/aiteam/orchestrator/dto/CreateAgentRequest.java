package com.aiteam.orchestrator.dto;

/**
 * 创建Agent请求DTO (Agent配置侧边栏)
 */
public class CreateAgentRequest {
    private String roleName;
    private String description;
    private String agentType;

    // Getters and Setters
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }
}