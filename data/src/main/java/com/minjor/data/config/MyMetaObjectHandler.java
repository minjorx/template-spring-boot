package com.minjor.data.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.minjor.data.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasGetter(BaseEntity.Fields.createAt)) {
            LocalDateTime now = LocalDateTime.now();
            this.strictInsertFill(metaObject, BaseEntity.Fields.createAt, LocalDateTime.class, now);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasGetter(BaseEntity.Fields.updateAt)) {
            LocalDateTime now = LocalDateTime.now();
            this.strictUpdateFill(metaObject, BaseEntity.Fields.updateAt, LocalDateTime.class, now);
        }
    }
}