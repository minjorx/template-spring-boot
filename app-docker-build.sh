#!/bin/bash
set -e

# 用法: ./run-jar.sh <模块目录>
# 示例: ./run-jar.sh user-service

# 检查参数
if [ $# -eq 0 ]; then
    echo "错误: 必须传入模块目录"
    echo "用法: $0 <模块目录>"
    exit 1
fi

MODULE_DIR="$1"
POM_FILE="$MODULE_DIR/pom.xml"

# 检查pom.xml是否存在
if [ ! -f "$POM_FILE" ]; then
    echo "错误: 找不到pom.xml: $POM_FILE"
    exit 1
fi

echo "模块目录: $MODULE_DIR"
echo "POM文件: $POM_FILE"

# 从pom.xml读取配置
APP_NAME=$(./mvnw -f "$POM_FILE" help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION=$(./mvnw -f "$POM_FILE" help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "应用名: $APP_NAME"
echo "版本: $VERSION"

# 查找target目录下的jar文件
TARGET_DIR="$MODULE_DIR/target"
JAR_FILE=""

# 查找最新的jar文件
if [ -d "$TARGET_DIR" ]; then
    # 查找非sources、非javadoc的jar文件
    JAR_FILE=$(find "$TARGET_DIR" -name "*.jar" ! -name "*sources*" ! -name "*javadoc*" -type f | head -1)
fi

if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
    echo "错误: 在 $TARGET_DIR 中找不到jar文件"
    echo "请先执行: mvn clean package"
    exit 1
fi

JAR_NAME=$(basename "$JAR_FILE")
echo "找到JAR文件: $JAR_NAME"

# 构建Docker镜像
echo "开始构建Docker镜像..."

# 关键点：
# 1. 使用--build-arg传递JAR文件名
# 2. 使用--build-arg传递WORKDIR（模块目录名称）
# 3. 设置构建上下文为模块目录的父目录
docker build \
    -f ./Dockerfile \
    --build-arg JAR_FILE="$JAR_NAME" \
    --build-arg WORKDIR="$MODULE_DIR" \
    -t "${APP_NAME}:${VERSION}" \
    "$(dirname "$MODULE_DIR")"  # 构建上下文设为模块目录的父目录

echo "Docker镜像构建完成: ${APP_NAME}:${VERSION}"