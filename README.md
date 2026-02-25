# AgentCorp SaaS Platform - AI Task Orchestration System

基于Spring Boot 3.x的AI任务编排系统，实现Dispatcher Agent拆解任务和Role-based Agent执行架构。

## 🚀 快速开始

### 方法一: 使用启动脚本 (推荐)
```bash
# 进入项目目录
cd /root/.openclaw/workspace/scope

# 启动应用
./startup.sh
```

### 方法二: 手动启动
```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 方法三: 使用IDE启动
1. 在IDE中打开项目
2. 运行 `AgentCorpApplication.java` 或 `com.aiteam.orchestrator.config.AgentCorpApplication`
3. 应用将自动启动

## 📋 启动前准备

### 1. 环境要求
- **Java 8+** (推荐Java 11)
- **Maven 3.6+**
- **网络连接** (用于OpenAI API调用)

### 2. 配置环境变量
```bash
# 设置OpenAI API Key
export OPENAI_API_KEY=your-openai-api-key

# 可选: 设置Redis连接
export REDIS_HOST=localhost
export REDIS_PORT=6379
```

### 3. 数据库配置
应用默认使用H2内存数据库，开发完成后可切换为MySQL/PostgreSQL：
- **H2控制台**: http://localhost:8080/api/h2-console
- **JDBC URL**: jdbc:h2:mem:agentcorp

## 🌐 访问接口

### API文档
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API基础路径**: http://localhost:8080/api

### 主要端点
```http
GET    /api/projects              # 获取项目列表
POST   /api/projects              # 创建新项目
GET    /api/projects/{id}         # 获取项目详情
POST   /api/projects/{id}/execute # 执行项目
GET    /api/agents                # 获取所有Agent
POST   /api/agents                # 创建Agent
PUT    /api/agents/{id}           # 更新Agent
```

### WebSocket
- **连接**: ws://localhost:8080/ws
- **项目状态**: ws://localhost:8080/ws/project/{projectId}

## 📊 监控与调试

### 健康检查
```bash
curl http://localhost:8080/api/actuator/health
```

### 日志查看
应用启动后会在控制台输出详细日志，包括：
- Dispatcher Agent任务拆解过程
- AgentOrchestrator执行状态
- WebSocket连接状态
- 数据库操作日志

## 🔧 配置选项

### application.yml
- **端口**: 8080 (可修改)
- **OpenAI配置**: API Key、模型选择
- **Redis配置**: 缓存和状态存储
- **数据库**: H2/MySQL/PostgreSQL支持

### 环境变量覆盖
```bash
# 覆盖配置文件设置
-Dspring.datasource.url=jdbc:mysql://localhost:3306/agentcorp
-Dspring.ai.openai.api-key=your-key
```

## 🚨 常见问题

### 启动失败
1. **检查Java版本**: `java -version`
2. **检查Maven**: `mvn --version`
3. **检查网络**: 确保可以访问OpenAI API

### OpenAI API错误
1. **检查API Key**: 确保环境变量正确设置
2. **检查网络**: OpenAI API可能需要代理
3. **使用测试Key**: 开发环境可使用测试Key

### 数据库连接失败
1. **H2模式**: 默认使用内存数据库，无需配置
2. **MySQL模式**: 确保数据库服务已启动
3. **Redis模式**: 确保Redis服务已启动

## 📚 开发指南

### 添加新功能
1. **创建Controller**: 在controller包下添加API接口
2. **创建DTO**: 在dto包下定义请求响应对象
3. **更新配置**: 在config包下添加必要配置
4. **运行测试**: `mvn test`

### 代码结构
```
src/main/java/com/aiteam/orchestrator/
├── controller/     # REST API控制器
├── dto/           # 数据传递对象
├── websocket/     # WebSocket服务
├── config/        # Spring配置类
└── orchestrator/  # 核心业务逻辑
```

## 🎯 项目状态

- ✅ 核心架构完成
- ✅ REST API实现
- ✅ WebSocket实时通信
- ✅ 数据库集成支持
- ✅ 配置和启动脚本
- ⏳ 生产环境部署

---

*AgentCorp SaaS Platform - 让AI团队协作变得简单高效*