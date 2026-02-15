package com.minjor.datam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minjor.data.entity.BaseEntity;
import com.minjor.data.enums.TaskStatus;
import com.minjor.datam.enums.ExecuteSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("datam_execute_history")
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteHistory extends BaseEntity {
    private String dsId;
    private ExecuteSource source;
    private String sourceId;
    private String sql;
    private Map<String, Object> params;
    private Long duration;
    private Integer resultSize;
    private TaskStatus status;
    private String traceId;
    private String spanId;
}
