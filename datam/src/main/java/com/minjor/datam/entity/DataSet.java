package com.minjor.datam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minjor.data.entity.BaseEntity;
import com.minjor.data.enums.DataStatus;
import com.minjor.datam.enums.DatasetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@TableName("datam_data_set")
@NoArgsConstructor
@AllArgsConstructor
public class DataSet extends BaseEntity {
    @TableField("deleted")
    private boolean deleted;
    private String dsId;
    private String code;
    private String table;
    private String name;
    private String description;
    private List<String> dimensions;
    private List<String> measures;
    private DatasetType type;
    private DataStatus status;
}
