package com.lz.realm;

import com.lz.entity.User;
import com.lz.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author 乐。
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    UserService userService;
    private String name;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        String userName = (String) principalCollection.getPrimaryPrincipal();

        User userInfo = userService.getByUserName(userName);

        if(userInfo!=null){
            simpleAuthorizationInfo.addRole(userInfo.getRole());

            simpleAuthorizationInfo.addStringPermission(userInfo.getPerms());
        }


        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String username = usernamePasswordToken.getUsername();
        String password = new String (usernamePasswordToken.getPassword());

        User userInfo = userService.getByUserName(username);

        if(userInfo == null){
            throw new AuthenticationException("用户名或者密码错误");
        }

        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username,userInfo.getPassword(), ByteSource.Util.bytes(username),getName());

        return simpleAuthenticationInfo;
    }

    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        return super.getAuthorizationCacheKey(principals);
    }

    @Override
    protected Object getAuthenticationCacheKey(PrincipalCollection principals) {
        return super.getAuthenticationCacheKey(principals);
    }
    public void clearCachedAuthorizationInfo(Object principal){
        Subject subject = SecurityUtils.getSubject();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
        subject.runAs(principals);
        clearCachedAuthorizationInfo(subject.getPrincipals());
        subject.releaseRunAs();

    }

    public void clearAllAuthenticationCacheKey(){
        if (getAuthenticationCache()!=null){
            getAuthenticationCache().clear();
        }
    }
    public void clearAllAuthorizationCacheKey(){
        if(getAuthorizationCache()!=null){
            getAuthorizationCache().clear();
        }
    }

}
