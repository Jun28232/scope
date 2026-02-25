package com.aiteam.orchestrator.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AgentCorp应用启动类
 * Spring Boot应用的主要启动配置
 */
@SpringBootApplication
@EnableScheduling
public class AgentCorpApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        // 设置OpenAI API Key环境变量（如果未设置）
        if (System.getenv("OPENAI_API_KEY") == null) {
            System.setProperty("spring.ai.openai.api-key", "sk-test-key");
            System.out.println("⚠️  警告: 未设置OPENAI_API_KEY环境变量，使用测试key");
            System.out.println("🔧 请在运行前设置环境变量: export OPENAI_API_KEY=your-api-key");
        }

        // 启动Spring Boot应用
        SpringApplication.run(AgentCorpApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AgentCorpApplication.class);
    }
}