package com.lz.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 乐。
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("invoke======>start insert fill");
        this.strictInsertFill(metaObject,"gmtCreate", Date.class,new Date());
        this.strictUpdateFill(metaObject,"gmtModify", Date.class,new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("invoke======>start update fill");
        this.strictUpdateFill(metaObject,"gmtModify", Date.class,new Date());
    }
}
