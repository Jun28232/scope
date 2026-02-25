package com.aiteam.orchestrator.dto;

import java.util.List;

/**
 * 批量更新响应DTO
 */
public class BatchUpdateResponse {
    private int updatedCount;
    private List<String> updatedAgentIds;

    // Getters and Setters
    public int getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }

    public List<String> getUpdatedAgentIds() { return updatedAgentIds; }
    public void setUpdatedAgentIds(List<String> updatedAgentIds) { this.updatedAgentIds = updatedAgentIds; }
}