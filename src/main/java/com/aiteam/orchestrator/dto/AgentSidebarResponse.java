package com.aiteam.orchestrator.dto;

import java.util.List;

/**
 * Agent侧边栏响应DTO (对应设计图页面2: Agent配置侧边栏)
 */
public class AgentSidebarResponse {
    private String agentId;
    private String roleName;
    private String description;
    private boolean active;
    private List<FormField> formFields;

    // Getters and Setters
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<FormField> getFormFields() { return formFields; }
    public void setFormFields(List<FormField> formFields) { this.formFields = formFields; }

    /**
     * 表单字段DTO (4个字段: 角色名称、角色描述、技能权限、性格参数)
     */
    public static class FormField {
        private String name;
        private String label;
        private String type;
        private String value;

        public FormField(String name, String label, String type, String value) {
            this.name = name;
            this.label = label;
            this.type = type;
            this.value = value;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}