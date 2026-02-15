后端业务模块设计 (按您的划分)

1. Datasource (数据源模块)

*   职责:
    *   连接信息管理: 存储、加密和管理各种数据源的连接配置（主机、端口、数据库、用户名、密码等）。
    *   连接器抽象: 定义统一的接口，封装不同数据库的驱动和操作逻辑（如JDBC、ODBC、特定SDK）。
    *   连接测试: 提供接口验证配置是否有效，能否成功建立连接。
    *   连接池管理: 为每个数据源实例维护连接池，优化连接复用，提高性能。
    *   元数据发现: 通过标准或特定SQL，扫描并获取数据源内的数据库、表、视图、字段等元数据信息。
    *   健康监控: 监控数据源连接状态。
*   核心组件/类:
    *   DataSourceModel: ORM模型，代表数据源实体。
    *   BaseConnector: 抽象基类，定义通用方法（connect(), disconnect(), get_schema() 等）。
    *   MySQLConnector, PostgreSQLConnector, BigQueryConnector 等: 具体的连接器实现。
    *   DataSourceManager: 业务逻辑层，负责CRUD操作、连接测试、元数据获取等。
*   主要接口 (API):
    *   POST /api/v1/datasources: 创建数据源。
    *   GET /api/v1/datasources: 列出数据源。
    *   GET /api/v1/datasources/{id}: 获取数据源详情。
    *   PUT /api/v1/datasources/{id}: 更新数据源。
    *   DELETE /api/v1/datasources/{id}: 删除数据源。
    *   POST /api/v1/datasources/{id}/test_connection: 测试连接。
    *   GET /api/v1/datasources/{id}/tables: 获取表列表。

2. Dataset (数据集模块)

*   职责:
    *   虚拟数据集管理: 存储用户定义的逻辑数据集，通常是一个指向特定数据源的SQL查询语句。
    *   列元数据管理: 定义和管理数据集中的列信息，如名称、数据类型、是否为维度/度量、格式、默认聚合方式等。
    *   SQL解析与校验: 对定义数据集的SQL进行语法校验和潜在风险（如注入）评估。
    *   依赖关系: 追踪数据集与其底层物理数据源/表的依赖关系。
*   核心组件/类:
    *   DatasetModel: ORM模型，代表数据集实体。
    *   DatasetManager: 业务逻辑层，负责CRUD、SQL校验、元数据获取等。
*   主要接口 (API):
    *   POST /api/v1/datasets: 创建数据集。
    *   GET /api/v1/datasets: 列出数据集。
    *   GET /api/v1/datasets/{id}: 获取数据集详情。
    *   PUT /api/v1/datasets/{id}: 更新数据集。
    *   DELETE /api/v1/datasets/{id}: 删除数据集。
    *   POST /api/v1/datasets/{id}/validate_sql: 校验SQL。

3. Query (查询模块)

*   职责:
    *   查询定义管理: 存储和管理用户提交的具体查询请求（可能来源于图表构建器或SQL Lab）。
    *   查询执行委托: 将查询请求传递给 Core.Executor 模块执行。
    *   查询历史记录: 记录用户的历史查询记录，便于重用和审计。
    *   参数化查询: 支持处理带有动态参数（如仪表盘筛选器）的查询。
*   核心组件/类:
    *   QueryModel: ORM模型，代表一次查询实例，可能包含SQL、数据源ID、参数、创建者等。
    *   QueryManager: 业务逻辑层，负责查询的创建、提交执行、历史管理等。
*   主要接口 (API):
    *   POST /api/v1/query: 提交一个新查询（可能返回执行ID用于异步获取结果）。
    *   GET /api/v1/query/{query_id}/results: 获取查询结果（可能需要结合Executor和Cache模块）。

4. Core (核心模块)

*   职责: 包含系统运行时的核心支撑功能。

4.1 Executor (执行器子模块)

*   职责:
    *   SQL执行: 接收来自 Query 模块或 Chart/Dashboard 模块的SQL请求，通过 Datasource 模块获取连接，在数据源上执行SQL。
    *   结果处理: 获取原始查询结果，进行格式化、类型转换等处理，使其适用于后续的数据展示或缓存。
    *   异步执行: 对于耗时长的查询，集成任务队列（如Celery）进行异步处理。
    *   查询限制: 实施超时、最大返回行数等限制，保障系统稳定性。
    *   错误处理: 捕获并处理执行过程中的数据库错误。
*   核心组件/类:
    *   QueryExecutor: 负责执行SQL的核心类。
    *   AsyncQueryTask: Celery任务，用于处理异步查询。
    *   ResultProcessor: 处理查询结果的工具类。
*   接口: 主要作为内部服务被 Query, Chart, Dashboard 模块调用，可能通过函数或Celery Task形式。

4.2 Audit (审计子模块)

*   职责:
    *   操作日志记录: 记录所有重要的用户操作和系统事件，如登录、资源创建/修改/删除、查询执行等。
    *   日志存储: 将审计日志写入专门的日志存储（可以是数据库表、日志文件或日志收集系统如ELK）。
    *   日志查询: 提供接口供管理员检索和分析审计日志。
*   核心组件/类:
    *   AuditLogModel: ORM模型，存储审计日志。
    *   AuditService: 提供日志记录和查询的通用方法，可被其他模块调用。
*   主要接口 (API):
    *   GET /api/v1/logs/audit: 查询审计日志。

4.3 Cache (缓存子模块)

*   职责:
    *   结果缓存: 缓存 Executor 模块执行的查询结果，当相同的查询再次发起时，直接从缓存返回，提升性能。
    *   配置缓存: 缓存 Datasource, Dataset 等模块的元数据或配置信息，减少数据库访问。
    *   缓存策略管理: 实现缓存键生成、TTL设置、缓存失效策略（如数据源更新时清除相关缓存）。
    *   缓存提供者: 抽象缓存后端（如Redis, Memcached），方便切换。
*   核心组件/类:
    *   CacheManager: 提供通用的缓存操作接口（get, set, delete, invalidate）。
    *   CacheProviderInterface, RedisCacheProvider: 抽象和具体实现。
*   接口: 主要作为内部服务被 Executor, Datasource, Dataset 等模块调用。
