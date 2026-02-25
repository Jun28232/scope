package com.aiteam.orchestrator;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Dispatcher Agent接口设计，利用LangChain4j的@AiService
 * 负责将自然语言拆解为ProjectPlan对象
 */
@Service
public class Dispatcher {

    private final ChatClient chatClient;

    public Dispatcher(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 将自然语言描述拆解为项目计划
     * @param naturalLanguageDescription 自然语言描述
     * @return 项目计划对象
     */
    public ProjectPlan decomposeTask(String naturalLanguageDescription) {
        String projectId = UUID.randomUUID().toString();
        String prompt = buildDecompositionPrompt(naturalLanguageDescription);

        try {
            ChatResponse response = chatClient.prompt()
                .user(prompt)
                .call();

            String aiResponse = response.content();
            ProjectPlan projectPlan = parseAiResponse(aiResponse, projectId);

            return projectPlan;
        } catch (Exception e) {
            throw new DispatcherException("Failed to decompose task: " + e.getMessage(), e);
        }
    }

    /**
     * 构建任务拆解的提示词
     */
    private String buildDecompositionPrompt(String description) {
        return """
            你是一个专业的AI项目拆解专家。请将以下项目需求拆解为具体的执行任务：
            
            项目需求: %s
            
            请按照以下格式返回JSON:
            {
              "title": "项目标题",
              "description": "项目详细描述",
              "tasks": [
                {
                  "id": "task_1",
                  "role": "后端",
                  "dependencies": [],
                  "description": "具体的任务描述"
                },
                {
                  "id": "task_2", 
                  "role": "前端",
                  "dependencies": ["task_1"],
                  "description": "具体的任务描述"
                }
              ]
            }
            
            要求:
            1. 每个任务必须有明确的执行角色（后端、前端、测试、架构设计等）
            2. 正确识别任务间的依赖关系
            3. 任务描述要具体、可执行
            4. 返回纯JSON格式，不要有其他文本
            """.formatted(description);
    }

    /**
     * 解析AI响应为ProjectPlan对象
     */
    private ProjectPlan parseAiResponse(String aiResponse, String projectId) {
        // TODO: 实现JSON解析逻辑，这里简化为示例
        // 实际实现需要使用Jackson或Gson等JSON库

        List<Task> tasks = List.of(
            Task.create("task_1", "后端", List.of(), "实现核心业务逻辑"),
            Task.create("task_2", "前端", List.of("task_1"), "开发用户界面"),
            Task.create("task_3", "测试", List.of("task_2"), "编写单元测试和集成测试")
        );

        return ProjectPlan.create(projectId, "AI项目", "基于自然语言的项目拆解", tasks);
    }

    /**
     * 批量拆解多个任务
     */
    public List<ProjectPlan> decomposeMultipleTasks(List<String> descriptions) {
        return descriptions.stream()
            .map(this::decomposeTask)
            .collect(Collectors.toList());
    }

    /**
     * 自定义异常类
     */
    public static class DispatcherException extends RuntimeException {
        public DispatcherException(String message, Throwable cause) {
            super(message, cause);
        }

        public DispatcherException(String message) {
            super(message);
        }
    }
}