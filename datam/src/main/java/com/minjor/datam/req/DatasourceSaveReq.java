package com.minjor.datam.req;

import com.minjor.datam.entity.Datasource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DatasourceSaveReq {
    @NotBlank(message = "名称不能为空")
    private String name;
    @NotBlank(message = "数据库名称不能为空")
    private String dbName;
    @NotBlank(message = "数据库地址不能为空")
    private String host;
    @NotBlank(message = "用户名不能为空")
    private String userName;
    @NotBlank(message = "密码不能为空")
    private String userPassword;
    @NotNull(message = "数据库端口不能为空")
    private Integer dbPort;
    @NotNull(message = "数据库类型不能为空")
    private Datasource.DbType dbType;
}
