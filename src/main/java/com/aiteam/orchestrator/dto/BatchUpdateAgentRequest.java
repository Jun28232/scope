package com.aiteam.orchestrator.dto;

import java.util.List;

/**
 * 批量更新Agent请求DTO (侧边栏批量操作)
 */
public class BatchUpdateAgentRequest {
    private List<String> agentIds;
    private boolean active;

    // Getters and Setters
    public List<String> getAgentIds() { return agentIds; }
    public void setAgentIds(List<String> agentIds) { this.agentIds = agentIds; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}