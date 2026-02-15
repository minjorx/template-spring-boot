CREATE TABLE datam_datasource (
    id CHAR(32) PRIMARY KEY,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    db_name VARCHAR(255) NOT NULL,
    host VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    properties JSONB,
    db_type VARCHAR(50) NOT NULL
);
-- 为表添加注释
COMMENT ON TABLE datam_datasource IS '数据源信息表';

-- 为字段添加注释
COMMENT ON COLUMN datam_datasource.id IS '主键ID';
COMMENT ON COLUMN datam_datasource.deleted IS '是否删除';
COMMENT ON COLUMN datam_datasource.create_at IS '创建时间';
COMMENT ON COLUMN datam_datasource.update_at IS '更新时间';
COMMENT ON COLUMN datam_datasource.name IS '数据源名称';
COMMENT ON COLUMN datam_datasource.status IS '数据源状态';
COMMENT ON COLUMN datam_datasource.db_name IS '数据库名称';
COMMENT ON COLUMN datam_datasource.host IS '主机地址';
COMMENT ON COLUMN datam_datasource.user_name IS '用户名';
COMMENT ON COLUMN datam_datasource.user_password IS '用户密码';
COMMENT ON COLUMN datam_datasource.port IS '端口号';
COMMENT ON COLUMN datam_datasource.properties IS '扩展属性（JSON格式）';
COMMENT ON COLUMN datam_datasource.db_type IS '数据库类型';


CREATE TABLE datam_execute_history (
    id CHAR(32) PRIMARY KEY,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    ds_id CHAR(32),
    source VARCHAR(50),
    source_id CHAR(32),
    sql TEXT NOT NULL,
    params JSONB,
    duration INTEGER,
    result_size INTEGER,
    status SMALLINT NOT NULL,
    trace_id CHAR(32),
    span_id CHAR(16)
);

COMMENT ON TABLE datam_execute_history IS '查询历史记录表';
COMMENT ON COLUMN datam_execute_history.id IS '主键ID';
COMMENT ON COLUMN datam_execute_history.create_at IS '创建时间';
COMMENT ON COLUMN datam_execute_history.update_at IS '更新时间';
COMMENT ON COLUMN datam_execute_history.ds_id IS '数据源ID';
COMMENT ON COLUMN datam_execute_history.source IS '查询来源';
COMMENT ON COLUMN datam_execute_history.source_id IS '来源ID';
COMMENT ON COLUMN datam_execute_history.sql IS 'SQL查询语句';
COMMENT ON COLUMN datam_execute_history.params IS '查询参数（JSON格式）';
COMMENT ON COLUMN datam_execute_history.duration IS '查询耗时（毫秒）';
COMMENT ON COLUMN datam_execute_history.result_size IS '查询结果大小';
COMMENT ON COLUMN datam_execute_history.status IS '任务状态';
COMMENT ON COLUMN datam_execute_history.trace_id IS '链路追踪ID';
COMMENT ON COLUMN datam_execute_history.span_id IS '链路追踪SpanID';

CREATE TABLE datam_data_set (
    id CHAR(32) PRIMARY KEY,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE,
    deleted BOOLEAN NOT NULL,
    ds_id VARCHAR(32),
    code VARCHAR(255),
    table_name VARCHAR(255),
    name VARCHAR(255),
    description TEXT,
    dimensions JSONB,
    measures JSONB,
    status VARCHAR(50)
);

COMMENT ON TABLE datam_data_set IS '数据集表';

COMMENT ON COLUMN datam_data_set.id IS '主键';
COMMENT ON COLUMN datam_data_set.create_at IS '创建时间';
COMMENT ON COLUMN datam_data_set.update_at IS '更新时间';
COMMENT ON COLUMN datam_data_set.deleted IS '是否删除';
COMMENT ON COLUMN datam_data_set.ds_id IS '数据源ID';
COMMENT ON COLUMN datam_data_set.code IS '编码';
COMMENT ON COLUMN datam_data_set.table_name IS '表名';
COMMENT ON COLUMN datam_data_set.name IS '名称';
COMMENT ON COLUMN datam_data_set.description IS '描述';
COMMENT ON COLUMN datam_data_set.dimensions IS '维度字段列表';
COMMENT ON COLUMN datam_data_set.measures IS '度量字段列表';
COMMENT ON COLUMN datam_data_set.status IS '状态';
