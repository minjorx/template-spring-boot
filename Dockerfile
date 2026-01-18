FROM minjor/alpine-openjdk25:1.0.0

# 接收构建时传递的参数
ARG JAR_FILE
ARG WORKDIR

# 设置工作目录（使用传入的模块目录名）
WORKDIR /${WORKDIR}

# 从构建上下文复制JAR文件到容器
# 注意：这里路径要加上模块目录前缀
COPY ${WORKDIR}/target/${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]