package com.lz.componment;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author 乐。
 */
public class JavaUuidEditSessionIdGenerator implements SessionIdGenerator {

    public JavaUuidEditSessionIdGenerator(){}
    @Override
    public Serializable generateId(Session session) {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
