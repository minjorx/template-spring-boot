#!/bin/bash
set -e

# Maven打包脚本
# 用法: ./build.sh <模块目录>
# 示例: ./build.sh user-service

if [ $# -eq 0 ]; then
    echo "错误: 必须传入模块目录"
    echo "用法: $0 <模块目录>"
    exit 1
fi

MODULE_DIR="$1"
POM_FILE="$MODULE_DIR/pom.xml"

if [ ! -f "$POM_FILE" ]; then
    echo "错误: 找不到pom.xml: $POM_FILE"
    exit 1
fi

echo "模块目录: $MODULE_DIR"
echo "POM文件: $POM_FILE"

# 执行Maven打包（使用-f参数指定pom.xml）
./mvnw -f "$POM_FILE" clean package