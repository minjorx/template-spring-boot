package com.minjor.datam.dto;

import com.minjor.data.enums.TaskStatus;
import com.minjor.datam.enums.ExecuteSource;
import lombok.Data;

import java.util.Map;

@Data
public class ExecuteHistoryLog {
    private String dsId;
    private ExecuteSource source;
    private String sourceId;
    private String sql;
    private Map<String, Object> params;
    private Long duration;
    private Integer resultSize;
    private TaskStatus status;
}
