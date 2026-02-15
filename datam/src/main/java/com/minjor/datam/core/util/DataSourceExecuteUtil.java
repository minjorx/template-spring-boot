package com.minjor.datam.core.util;

import com.minjor.common.utils.JacksonUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import tools.jackson.core.type.TypeReference;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class DataSourceExecuteUtil {

    public static List<Map<String, Object>> rsToMapList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }
            results.add(row);
        }
        return results;
    }

    /**
     * 执行SQL查询并返回表格类型的数据（List<Map<String, Object>>）
     *
     * @param dataSource 数据源
     * @param sql        SQL查询语句
     * @param args       查询参数
     * @return 查询结果，每行数据为一个Map，key为列名，value为列值
     */
    public static List<Map<String, Object>> queryForList(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            Object[] args) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // 设置参数
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    preparedStatement.setObject(i + 1, args[i]);
                }
            }

            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rsToMapList(rs);
            }
        }
    }

    /**
     * 执行SQL查询并返回表格类型的数据（List<Map<String, Object>>）
     *
     * @param dataSource 数据源
     * @param sql        SQL查询语句
     * @return 查询结果，每行数据为一个Map，key为列名，value为列值
     */
    public static List<Map<String, Object>> queryForList(
            @NonNull DataSource dataSource,
            @NonNull String sql) throws SQLException {
        return queryForList(dataSource, sql, (Object[]) null);
    }

    /**
     * 执行SQL查询并返回指定类型的列表（基于TypeReference）
     *
     * @param dataSource    数据源
     * @param sql           SQL查询语句
     * @param args          查询参数
     * @param typeReference 目标类型的TypeReference
     * @param <T>           返回列表的元素类型
     * @return 查询结果列表
     */
    public static <T> List<T> queryForList(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            Object[] args,
            @NonNull TypeReference<T> typeReference) throws SQLException {

        // 获取表格数据
        List<Map<String, Object>> tableData = queryForList(dataSource, sql, args);

        // 使用Jackson进行类型转换
        List<T> results = new ArrayList<>();
        for (Map<String, Object> row : tableData) {
            T instance = JacksonUtil.getJsonMapper().convertValue(row, typeReference);
            results.add(instance);
        }
        return results;
    }

    /**
     * 执行SQL查询并返回指定类型的列表（基于TypeReference）
     *
     * @param dataSource    数据源
     * @param sql           SQL查询语句
     * @param typeReference 目标类型的TypeReference
     * @param <T>           返回列表的元素类型
     * @return 查询结果列表
     */
    public static <T> List<T> queryForList(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            @NonNull TypeReference<T> typeReference) throws SQLException {

        // 获取表格数据
        List<Map<String, Object>> tableData = queryForList(dataSource, sql);

        // 使用Jackson进行类型转换
        List<T> results = new ArrayList<>();
        for (Map<String, Object> row : tableData) {
            T instance = JacksonUtil.getJsonMapper().convertValue(row, typeReference);
            results.add(instance);
        }
        return results;
    }

    /**
     * 执行SQL查询并返回指定类型的列表（基于TypeReference）
     *
     * @param dataSource    数据源
     * @param sql           SQL查询语句
     * @param args          查询参数
     * @param tClass        目标类型的Class
     * @param <T>           返回列表的元素类型
     * @return 查询结果列表
     */
    public static <T> List<T> queryForList(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            Object[] args,
            @NonNull Class<T> tClass) throws SQLException {

        // 获取表格数据
        List<Map<String, Object>> tableData = queryForList(dataSource, sql, args);

        // 使用Jackson进行类型转换
        List<T> results = new ArrayList<>();
        for (Map<String, Object> row : tableData) {
            T instance = JacksonUtil.getJsonMapper().convertValue(row, tClass);
            results.add(instance);
        }
        return results;
    }

    /**
     * 执行SQL查询并返回指定类型的列表（基于TypeReference）
     *
     * @param dataSource    数据源
     * @param sql           SQL查询语句
     * @param tClass        目标类型的Class
     * @param <T>           返回列表的元素类型
     * @return 查询结果列表
     */
    public static <T> List<T> queryForList(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            @NonNull Class<T> tClass) throws SQLException {

        // 获取表格数据
        List<Map<String, Object>> tableData = queryForList(dataSource, sql);

        // 使用Jackson进行类型转换
        List<T> results = new ArrayList<>();
        for (Map<String, Object> row : tableData) {
            T instance = JacksonUtil.getJsonMapper().convertValue(row, tClass);
            results.add(instance);
        }
        return results;
    }

    /**
     * 执行SQL查询并返回单个对象（基于TypeReference）
     *
     * @param dataSource    数据源
     * @param sql           SQL查询语句
     * @param args          查询参数
     * @param typeReference 目标类型的TypeReference
     * @param <T>           返回对象的类型
     * @return 查询结果对象
     */
    public static <T> T queryForObject(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            Object[] args,
            @NonNull TypeReference<T> typeReference) throws SQLException {

        // 获取表格数据
        List<Map<String, Object>> tableData = queryForList(dataSource, sql, args);

        // 如果没有数据，返回null
        if (tableData.isEmpty()) {
            return null;
        }

        // 只取第一行数据进行转换
        Map<String, Object> row = tableData.getFirst();
        return JacksonUtil.getJsonMapper().convertValue(row, typeReference);
    }

    /**
     * 执行SQL查询并返回单个对象（基于TypeReference）
     *
     * @param dataSource    数据源
     * @param sql           SQL查询语句
     * @param typeReference 目标类型的TypeReference
     * @param <T>           返回对象的类型
     * @return 查询结果对象
     */
    public static <T> T queryForObject(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            @NonNull TypeReference<T> typeReference) throws SQLException {

        // 获取表格数据
        List<Map<String, Object>> tableData = queryForList(dataSource, sql);

        // 如果没有数据，返回null
        if (tableData.isEmpty()) {
            return null;
        }

        // 只取第一行数据进行转换
        Map<String, Object> row = tableData.getFirst();
        return JacksonUtil.getJsonMapper().convertValue(row, typeReference);
    }

    /**
     * 执行SQL查询并返回单个对象（基于Class<T>）
     *
     * @param dataSource 数据源
     * @param sql        SQL查询语句
     * @param args       查询参数
     * @param tClass     目标类型的Class
     * @param <T>        返回对象的类型
     * @return 查询结果对象
     */
    public static <T> T queryForObject(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            Object[] args,
            @NonNull Class<T> tClass) throws SQLException {

        // 获取表格数据
        List<Map<String, Object>> tableData = queryForList(dataSource, sql, args);

        // 如果没有数据，返回null
        if (tableData.isEmpty()) {
            return null;
        }

        // 只取第一行数据进行转换
        Map<String, Object> row = tableData.getFirst();
        return JacksonUtil.getJsonMapper().convertValue(row, tClass);
    }

    /**
     * 执行SQL查询并返回单个对象（基于Class<T>）
     *
     * @param dataSource 数据源
     * @param sql        SQL查询语句
     * @param tClass     目标类型的Class
     * @param <T>        返回对象的类型
     * @return 查询结果对象
     */
    public static <T> T queryForObject(
            @NonNull DataSource dataSource,
            @NonNull String sql,
            @NonNull Class<T> tClass) throws SQLException {

        // 获取表格数据
        List<Map<String, Object>> tableData = queryForList(dataSource, sql);

        // 如果没有数据，返回null
        if (tableData.isEmpty()) {
            return null;
        }

        // 只取第一行数据进行转换
        Map<String, Object> row = tableData.getFirst();
        return JacksonUtil.getJsonMapper().convertValue(row, tClass);
    }

}