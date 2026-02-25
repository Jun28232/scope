package com.aiteam.orchestrator;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Arrays;

/**
 * Agent仓库接口，负责从数据库获取Agent信息
 */
@Repository
public interface AgentRepository {

    /**
     * 获取所有可用的Agent
     */
    List<Agent> findAll();

    /**
     * 根据角色名称获取Agent
     */
    Agent findByRoleName(String roleName);

    /**
     * 根据ID获取Agent
     */
    Agent findById(String id);

    /**
     * 保存Agent
     */
    void save(Agent agent);

    /**
     * 删除Agent
     */
    void delete(String id);

    /**
     * 检查Agent是否存在
     */
    boolean exists(String id);
}

// 数据库实现示例
class DatabaseAgentRepository implements AgentRepository {

    // TODO: 实际实现需要使用JPA或MyBatis等ORM框架

    @Override
    public List<Agent> findAll() {
        // 这里返回默认的代理配置
        return Arrays.asList(
            new Agent("1", "后端", "BACKEND", "负责后端开发任务", true),
            new Agent("2", "前端", "FRONTEND", "负责前端开发任务", true),
            new Agent("3", "测试", "TESTING", "负责测试任务", true),
            new Agent("4", "架构设计", "ARCHITECTURE", "负责架构设计任务", true)
        );
    }

    @Override
    public Agent findByRoleName(String roleName) {
        return findAll().stream()
            .filter(agent -> agent.getRoleName().equals(roleName))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Agent findById(String id) {
        return findAll().stream()
            .filter(agent -> agent.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    @Override
    public void save(Agent agent) {
        // TODO: 实现数据库保存逻辑
        System.out.println("Agent saved: " + agent.getRoleName());
    }

    @Override
    public void delete(String id) {
        // TODO: 实现数据库删除逻辑
        System.out.println("Agent deleted: " + id);
    }

    @Override
    public boolean exists(String id) {
        return findById(id) != null;
    }
}