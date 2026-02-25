#!/bin/bash

# AgentCorp SaaS Platform 启动脚本
echo "🚀 正在启动 AgentCorp SaaS Platform..."

# 检查Java版本
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 8 ]; then
    echo "❌ 错误: 需要Java 8或更高版本，当前版本: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

# 检查环境变量
if [ -z "$OPENAI_API_KEY" ]; then
    echo "⚠️  警告: 未设置OPENAI_API_KEY环境变量"
    echo "🔧 请在运行前设置: export OPENAI_API_KEY=your-api-key"
    echo "💡 或者使用默认测试key启动"
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven，请先安装Maven"
    exit 1
fi

# 编译项目
echo "📦 正在编译项目..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败，请检查错误信息"
    exit 1
fi

# 启动应用
echo "🚀 正在启动应用..."
echo "📊 访问API文档: http://localhost:8080/api/swagger-ui.html"
echo "💾 访问数据库控制台: http://localhost:8080/api/h2-console"
echo "🔌 WebSocket端点: ws://localhost:8080/ws"
echo "📈 监控端点: http://localhost:8080/api/actuator/health"

mvn spring-boot:run

echo "✅ 应用已停止"