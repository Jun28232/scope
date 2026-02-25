package com.aiteam.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.chat.client.ChatClient;

/**
 * AI外包公司系统主应用
 */
@SpringBootApplication
public class AIOrchestrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIOrchestrationApplication.class, args);
    }

    /**
     * 配置ChatClient Bean
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

    /**
     * 创建默认的项目状态仓库实现
     */
    @Bean
    public ProjectStateRepository projectStateRepository() {
        return new RedisProjectStateRepository();
    }

    /**
     * 创建默认的Agent仓库实现
     */
    @Bean
    public AgentRepository agentRepository() {
        return new DatabaseAgentRepository();
    }
}