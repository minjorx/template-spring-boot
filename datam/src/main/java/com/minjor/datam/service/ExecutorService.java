package com.minjor.datam.service;

import com.minjor.data.enums.TaskStatus;
import com.minjor.datam.core.datasource.DataSourceFactory;
import com.minjor.datam.dto.ExecuteHistoryLog;
import com.minjor.datam.enums.ExecuteSource;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExecutorService {

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Autowired
    private ExecuteHistoryService executeHistoryService;

    public List<Map<String, Object>> tryExecuteSql(
            @NonNull String dsId,
            @NonNull String sql,
            @Nullable Map<String, Object> params) {
        return executeSql(dsId, sql, params, ExecuteSource.MANUAL, null);
    }

    public List<Map<String, Object>> executeSql(
            @NonNull String dsId,
            @NonNull String sql,
            @Nullable Map<String, Object> params,
            @NonNull ExecuteSource source,
            @Nullable String sourceId) {
        ExecuteHistoryLog executeHistoryLog = new ExecuteHistoryLog();
        executeHistoryLog.setDsId(dsId);
        executeHistoryLog.setSql(sql);
        executeHistoryLog.setSource(source);
        executeHistoryLog.setSourceId(sourceId);
        try {
            List<Map<String, Object>> result;
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            if (CollectionUtils.isEmpty(params)) {
                result = dataSourceFactory.getJdbcTemplate(dsId).queryForList(sql);
            } else {
                result = dataSourceFactory.getJdbcTemplate(dsId).queryForList(sql, params);
            }
            stopWatch.stop();
            executeHistoryLog.setDuration(stopWatch.getTotalTimeMillis());
            executeHistoryLog.setResultSize(result.size());
            executeHistoryLog.setStatus(TaskStatus.ACTIVE);
            return result;
        } catch (Exception e){
            log.error("执行SQL异常: {}", e.getMessage(), e);
            executeHistoryLog.setStatus(TaskStatus.INACTIVE);
            throw new RuntimeException("执行SQL异常: " + e.getMessage());
        } finally {
            executeHistoryService.asyncLog(executeHistoryLog);
        }
    }
}
