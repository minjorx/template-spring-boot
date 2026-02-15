package com.minjor.datam.dto;

import lombok.Data;

import java.sql.Types;

@Data
public class ColumnInfo {
    private final String colName;
    private final Class<?> javaType;
    private final String dbTypeName;

    public ColumnInfo(String colName, int jdbcType, String dbTypeName) {
        this.colName = colName;
        this.javaType = mapJdbcTypeToJavaClass(jdbcType);
        this.dbTypeName = dbTypeName;
    }

    /**
     * 根据 JDBC 数据类型码映射到对应的 Java 类。
     * 注意：此映射是一个通用近似，具体实现可能因驱动而异。
     *
     * @param jdbcType JDBC 类型码 (来自 java.sql.Types)
     * @return 对应的 Java 类
     */
    private static Class<?> mapJdbcTypeToJavaClass(int jdbcType) {
        return switch (jdbcType) {
            case Types.BIT, Types.BOOLEAN -> Boolean.class; // JDBC BOOLEAN 映射到 Java Boolean
            case Types.TINYINT -> Byte.class; // JDBC TINYINT 映射到 Java Byte
            case Types.SMALLINT -> Short.class; // JDBC SMALLINT 映射到 Java Short
            case Types.INTEGER -> Integer.class; // JDBC INTEGER 映射到 Java Integer
            case Types.BIGINT -> Long.class; // JDBC BIGINT 映射到 Java Long
            case Types.FLOAT, Types.REAL -> // REAL is often treated as Float in many contexts
                    Float.class; // JDBC FLOAT/REAL 映射到 Java Float
            case Types.DOUBLE, Types.NUMERIC,
                 Types.DECIMAL -> // DECIMAL is typically mapped to BigDecimal for precision
                    Double.class; // JDBC DOUBLE/NUMERIC/DECIMAL 映射到 Java Double (或 BigDecimal)
            case Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.LONGNVARCHAR ->
                    String.class; // 大多数文本类型映射到 Java String
            case Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY, Types.BLOB ->
                    byte[].class; // 二进制数据类型映射到 Java byte array
            case Types.DATE -> java.sql.Date.class; // JDBC DATE 映射到 Java sql.Date
            case Types.TIME -> java.sql.Time.class; // JDBC TIME 映射到 Java sql.Time
            case Types.TIMESTAMP -> java.sql.Timestamp.class; // JDBC TIMESTAMP 映射到 Java sql.Timestamp
            case Types.CLOB -> java.sql.Clob.class; // JDBC CLOB 映射到 Java Clob
            case Types.ARRAY -> java.sql.Array.class; // JDBC ARRAY 映射到 Java Array
            case Types.STRUCT -> java.sql.Struct.class; // JDBC STRUCT 映射到 Java Struct
            case Types.REF -> java.sql.Ref.class; // JDBC REF 映射到 Java Ref
            case Types.DISTINCT, Types.JAVA_OBJECT -> Object.class; // JAVA_OBJECT/DISTINCT 通常映射到 Object 或特定类型
            default ->
                    Object.class;
        };
    }
}