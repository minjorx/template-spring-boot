package com.minjor.datam.entity;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minjor.data.entity.BaseEntity;
import com.minjor.common.enums.BaseEnum;
import com.minjor.data.enums.DataStatus;
import lombok.*;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("datam_datasource")
@NoArgsConstructor
@AllArgsConstructor
public class Datasource extends BaseEntity {
    @TableField("deleted")
    private boolean deleted;
    private String name;
    private DataStatus status;
    private String dbName;
    private String host;
    private String userName;
    private String userPassword;
    private Integer port;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> properties;

    private DbType dbType;

    public String getUrl() {
        return switch (dbType) {
            case POSTGRESQL -> "jdbc:postgresql://%s:%s/%s".formatted(host, port, dbName);
        };
    }

    @AllArgsConstructor
    @Getter
    public enum DbType implements BaseEnum<String, String>, IEnum<String> {
        POSTGRESQL("PostgreSQL", "POSTGRESQL",
                "org.postgresql.Driver",
                "SELECT 1",
                new String[]{"TABLE", "VIEW", "MATERIALIZED VIEW"}),
        ;
        private final String label;
        private final String value;

        @JsonIgnore
        private final String driverClassName;

        @JsonIgnore
        private final String validationQuery;

        @JsonIgnore
        private final String[] types;
    }
}
