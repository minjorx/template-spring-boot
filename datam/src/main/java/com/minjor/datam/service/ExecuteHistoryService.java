package com.minjor.datam.service;

import com.minjor.datam.dto.ExecuteHistoryLog;
import com.minjor.datam.entity.ExecuteHistory;
import com.minjor.datam.mapper.ExecuteHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExecuteHistoryService {
    @Autowired
    private ExecuteHistoryMapper executeHistoryMapper;

    @Async
    public void asyncLog(ExecuteHistoryLog executeHistoryLog) {
        ExecuteHistory executeHistory = new ExecuteHistory();
        executeHistory.setDsId(executeHistoryLog.getDsId());
        executeHistory.setSql(executeHistoryLog.getSql());
        executeHistory.setSource(executeHistoryLog.getSource());
        executeHistory.setSourceId(executeHistoryLog.getSourceId());
        executeHistory.setStatus(executeHistoryLog.getStatus());
        executeHistory.setDuration(executeHistoryLog.getDuration());
        executeHistory.setResultSize(executeHistoryLog.getResultSize());
        executeHistory.setParams(executeHistoryLog.getParams());
//        log.info("执行SQL=> dsId: {}, sql: {}, source: {}, sourceId: {}, status: {}, duration: {}, resultSize: {}",
//                executeHistory.getDsId(),
//                executeHistory.getSql(),
//                executeHistory.getSource(),
//                executeHistory.getSourceId(),
//                executeHistory.getStatus(),
//                executeHistory.getDuration(),
//                executeHistory.getResultSize());
        executeHistory.setTraceId(MDC.get("traceId"));
        executeHistory.setSpanId(MDC.get("spanId"));
        executeHistoryMapper.insert(executeHistory);
    }
}
