package com.aiteam.orchestrator.controller;

import com.aiteam.orchestrator.Agent;
import com.aiteam.orchestrator.AgentRepository;
import com.aiteam.orchestrator.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Agent控制器 - 实现AgentCorp SaaS平台的Agent管理功能
 * (对应设计图页面2: Agent配置侧边栏)
 */
@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "*")
public class AgentController {

    @Autowired
    private AgentRepository agentRepository;

    /**
     * 获取所有Agent (组织架构管理页面)
     */
    @GetMapping
    public ResponseEntity<List<AgentResponse>> getAllAgents() {
        try {
            List<AgentResponse> responses = agentRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 创建新Agent (Agent配置侧边栏)
     */
    @PostMapping
    public ResponseEntity<AgentResponse> createAgent(@RequestBody CreateAgentRequest request) {
        try {
            Agent agent = new Agent();
            agent.setId(java.util.UUID.randomUUID().toString());
            agent.setRoleName(request.getRoleName());
            agent.setAgentType(request.getAgentType());
            agent.setDescription(request.getDescription());
            agent.setActive(true);

            agentRepository.save(agent);

            AgentResponse response = convertToResponse(agent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新Agent (Agent配置侧边栏)
     */
    @PutMapping("/{agentId}")
    public ResponseEntity<AgentResponse> updateAgent(
            @PathVariable String agentId,
            @RequestBody UpdateAgentRequest request) {
        try {
            Agent agent = agentRepository.findById(agentId);
            if (agent == null) {
                return ResponseEntity.notFound().build();
            }

            agent.setRoleName(request.getRoleName());
            agent.setAgentType(request.getAgentType());
            agent.setDescription(request.getDescription());
            agent.setActive(request.isActive());

            agentRepository.save(agent);

            AgentResponse response = convertToResponse(agent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除Agent
     */
    @DeleteMapping("/{agentId}")
    public ResponseEntity<Void> deleteAgent(@PathVariable String agentId) {
        try {
            agentRepository.delete(agentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据角色名称获取Agent (侧边栏配置)
     */
    @GetMapping("/role/{roleName}")
    public ResponseEntity<AgentSidebarResponse> getAgentByRole(@PathVariable String roleName) {
        try {
            Agent agent = agentRepository.findByRoleName(roleName);
            if (agent == null) {
                return ResponseEntity.notFound().build();
            }

            AgentSidebarResponse response = new AgentSidebarResponse();
            response.setAgentId(agent.getId());
            response.setRoleName(agent.getRoleName());
            response.setDescription(agent.getDescription());
            response.setActive(agent.isActive());

            // 配置表单字段
            response.setFormFields(java.util.Arrays.asList(
                new FormField("roleName", "角色名称", "text", agent.getRoleName()),
                new FormField("description", "角色描述", "textarea", agent.getDescription()),
                new FormField("agentType", "代理类型", "select", agent.getAgentType()),
                new FormField("active", "启用状态", "checkbox", String.valueOf(agent.isActive()))
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量更新Agent配置 (侧边栏批量操作)
     */
    @PostMapping("/batch-update")
    public ResponseEntity<BatchUpdateResponse> batchUpdateAgents(
            @RequestBody BatchUpdateAgentRequest request) {
        try {
            List<String> updatedIds = request.getAgentIds().stream()
                .map(id -> {
                    Agent agent = agentRepository.findById(id);
                    if (agent != null) {
                        agent.setActive(request.isActive());
                        agentRepository.save(agent);
                    }
                    return id;
                })
                .toList();

            BatchUpdateResponse response = new BatchUpdateResponse();
            response.setUpdatedCount(updatedIds.size());
            response.setUpdatedAgentIds(updatedIds);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Agent实体转响应对象
     */
    private AgentResponse convertToResponse(Agent agent) {
        AgentResponse response = new AgentResponse();
        response.setAgentId(agent.getId());
        response.setRoleName(agent.getRoleName());
        response.setAgentType(agent.getAgentType());
        response.setDescription(agent.getDescription());
        response.setActive(agent.isActive());
        response.setCreatedAt(java.time.LocalDateTime.now());
        response.setUpdatedAt(java.time.LocalDateTime.now());

        return response;
    }
}