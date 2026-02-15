package com.minjor.datam.core.datasource;

import com.minjor.datam.entity.Datasource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceFactory {

    private final Map<String, DataSourceInstance> dataSourceCache = new ConcurrentHashMap<>();

    public void connectDataSource(
            String uuid,
            String url,
            String user,
            String password,
            Map<String, Object> properties,
            Datasource.DbType dbType
    ) {

        HikariDataSource dataSource = getDataSource(uuid, url, user, password, properties, dbType);

        // 存储到缓存
        dataSourceCache.put(uuid, new DataSourceInstance(dataSource, new JdbcTemplate(dataSource)));
    }

    private static @NonNull HikariDataSource getDataSource(String id, String url, String user, String password, Map<String, Object> properties, Datasource.DbType dbType) {
        // 创建 HikariCP 配置
        HikariConfig config = new HikariConfig();
        config.setPoolName("datam-datasource-" + id);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setConnectionTestQuery(dbType.getValidationQuery());
        config.setDriverClassName(dbType.getDriverClassName());

        // 设置其他属性
        if (!CollectionUtils.isEmpty(properties)) {
            properties.forEach(config::addDataSourceProperty);
        }
        // 创建数据源
        return new HikariDataSource(config);
    }


    /**
     * 测试指定参数的数据源连接是否可用（不注册到缓存）
     *
     * @param url 数据库连接URL
     * @param user 用户名
     * @param password 密码
     * @param dbType 数据库类型
     * @return 连接是否成功
     */
    public boolean testConnection(
            String url,
            String user,
            String password,
            Map<String, Object> properties,
            Datasource.DbType dbType
    ) {
        String randomId = "test-" + System.currentTimeMillis();
        HikariConfig config = getDataSource(randomId, url, user, password, properties, dbType);

        // 设置较短的超时时间用于测试
        config.setConnectionTimeout(10000); // 10秒连接超时
        config.setMaximumPoolSize(2);       // 最小连接池大小

        try (HikariDataSource tempDataSource = new HikariDataSource(config);
             Connection connection = tempDataSource.getConnection()) {
            return connection.isValid(3); // 等待最多3秒来验证连接
        } catch (SQLException e) {
            return false;
        }
    }

    public DataSource getDataSource(String uuid) {
        return dataSourceCache.get(uuid).dataSource();
    }

    public JdbcTemplate getJdbcTemplate(String uuid) {
        return dataSourceCache.get(uuid).jdbcTemplate();
    }

    public void closeDataSource(String id) {
        DataSourceInstance dataSourceInstance = dataSourceCache.remove(id);
        if (dataSourceInstance.dataSource() != null) {
            if (dataSourceInstance.dataSource() instanceof HikariDataSource dataSource) {
                dataSource.close();
            }
        }

    }

    public boolean exist(String id) {
        return dataSourceCache.containsKey(id);
    }

    private record DataSourceInstance(DataSource dataSource, JdbcTemplate jdbcTemplate) {
    }
}
