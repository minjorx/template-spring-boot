package com.minjor.datam.service;

import com.minjor.common.model.LabelValue;
import com.minjor.data.enums.DataStatus;
import com.minjor.datam.core.util.DataSourceExecuteUtil;
import com.minjor.datam.dto.ColumnInfo;
import com.minjor.datam.entity.Datasource;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MetaDataService {
    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private DataSourceConnectionService dataSourceConnectionService;

    public List<LabelValue<String, String, String>> labelValues(String dsId, String name) {
        Datasource datasource = datasourceService.get(dsId);
        Assert.notNull(datasource, "数据源不存在");
        Assert.isTrue(Objects.equals(datasource.getStatus(), DataStatus.ACTIVE), "数据源未启用");
        String catalog = datasource.getDbName();
        String schema = getDefaultSchema(datasource);
        Datasource.DbType dt = datasource.getDbType();
        try (Connection connection = dataSourceConnectionService.getDataSource(dsId).getConnection()){
            ResultSet rs = connection.getMetaData()
                    .getTables(catalog, schema, name, dt.getTypes());
            return DataSourceExecuteUtil
                    .rsToMapList(rs)
                    .stream().map(map -> new LabelValue<>(
                            (String) map.get("TABLE_NAME"),
                            (String) map.get("TABLE_NAME"),
                            (String) map.get("REMARKS")
                    ))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private @Nullable String getDefaultSchema(Datasource datasource) {
        String schema = Optional.ofNullable(datasource.getProperties())
                .map(m -> m.get("currentSchema"))
                .map(Object::toString)
                .orElse( null);
        if (datasource.getDbType().equals(Datasource.DbType.POSTGRESQL) && Strings.isBlank(schema)) {
            return "public";
        }
        return schema;
    }

    public List<ColumnInfo> columns(String dsId, String tableName) {
        Datasource datasource = datasourceService.get(dsId);
        Assert.notNull(datasource, "数据源不存在");
        Assert.isTrue(Objects.equals(datasource.getStatus(), DataStatus.ACTIVE), "数据源未启用");
        String catalog = datasource.getDbName();
        String schema = getDefaultSchema(datasource);
        try (Connection connection = dataSourceConnectionService.getDataSource(dsId).getConnection()){
            ResultSet rs = connection.getMetaData()
                    .getColumns(catalog, schema, tableName, null);
            return DataSourceExecuteUtil
                    .rsToMapList(rs)
                    .stream().map(item -> new ColumnInfo(
                            (String) item.get("COLUMN_NAME"),
                            (Integer) item.get("DATA_TYPE"),
                            (String) item.get("TYPE_NAME")
                    )).toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
