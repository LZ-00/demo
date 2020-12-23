package com.lz.componment;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * @author 乐。
 */
@Slf4j
public class ShiroWebSessionManager extends DefaultWebSessionManager {

    private static final String AUTHORIZATION="token";
    private static final String REFERENCED_SESSION_ID_SOURCE="header";

    public ShiroWebSessionManager(){
        super();
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {

        String id = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
        //如果请求头中有Authorization,则其值为SessionId
        if(!StringUtils.isBlank(id)){
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        }else {
            //否则按默认规则取sessionId
            return super.getSessionId(request,response);
        }
    }

    /**
     * 获取session
     * 优化单次请求需要多次访问redis问题
     * @param sessionKey
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);

        ServletRequest request = null;
        if(sessionKey instanceof WebSessionKey){
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }
        if(request!=null && null != sessionId){
            Object sessionObj = request.getAttribute(sessionId.toString());
            if(sessionObj!=null){
                log.debug("read session from request");
                return (Session) sessionObj;
            }
        }
        Session session = super.retrieveSession(sessionKey);
        if(request!=null && null != sessionId){
            request.setAttribute(sessionId.toString(),session);
        }
        return session;
    }
}
