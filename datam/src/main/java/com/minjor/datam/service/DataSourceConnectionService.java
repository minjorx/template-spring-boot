package com.minjor.datam.service;

import com.minjor.datam.core.datasource.DataSourceFactory;
import com.minjor.datam.entity.Datasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@Slf4j
public class DataSourceConnectionService {
    @Autowired
    DataSourceFactory dataSourceFactory;

    public DataSource getDataSource(String id) {
        return dataSourceFactory.getDataSource(id);
    }

    public JdbcTemplate getJdbcTemplate(String id) {
        return dataSourceFactory.getJdbcTemplate(id);
    }

    public boolean exist(String id) {
        return dataSourceFactory.exist(id);
    }

    @Async
    public void connectDataSource(Datasource datasource) {
        log.info("connectDataSource: {}", datasource);
        dataSourceFactory.connectDataSource(
                datasource.getId(),
                datasource.getUrl(),
                datasource.getUserName(),
                datasource.getUserPassword(),
                datasource.getProperties(),
                datasource.getDbType()
        );
    }

    public void closeDataSource(String id) {
        log.info("closeDataSource: {}", id);
        dataSourceFactory.closeDataSource(id);
    }

    public boolean testConnection(Datasource datasource) {
        return dataSourceFactory.testConnection(
                datasource.getUrl(),
                datasource.getUserName(),
                datasource.getUserPassword(),
                datasource.getProperties(),
                datasource.getDbType()
        );
    }
}
