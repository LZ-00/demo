package com.lz.config;

import com.lz.componment.JavaUuidEditSessionIdGenerator;
import com.lz.componment.ShiroWebSessionManager;
import com.lz.filter.KickoutSessionControlFilter;
import com.lz.realm.ShiroRealm;
import com.lz.util.RedisUtil;
import com.lz.util.securityutil.SystemUtil;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import java.util.LinkedHashMap;

/**
 * @author 乐。
 */
@Component
public class ShiroConfig {

    @Autowired
    RedisUtil redisUtil;

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);

        shiroFilterFactoryBean.setUnauthorizedUrl("/unAuthorized");
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        shiroFilterFactoryBean.setSuccessUrl("/");

        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();

        filtersMap.put("kickout", kickoutSessionControlFilter());

        shiroFilterFactoryBean.setFilters(filtersMap);

        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        filterChainDefinitionMap.put("/login","anon");

        filterChainDefinitionMap.put("/css/**","anon");
        filterChainDefinitionMap.put("/js/**","anon");
        filterChainDefinitionMap.put("/image/**","anon");
        filterChainDefinitionMap.put("/druid/**","anon");

        //放行Swagger接口
        filterChainDefinitionMap.put("/swagger**/**","anon");
        filterChainDefinitionMap.put("/v3/**","anon");
        filterChainDefinitionMap.put("/webjars/**","anon");
        filterChainDefinitionMap.put("/doc.html","anon");
        //logout是Shiro提供的过滤器
//        filterChainDefinitionMap.put("/logout","logout");

        //其他资源都需要认证
        filterChainDefinitionMap.put("/**","kickout,authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;

    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(ShiroRealm shiroRealm){

        DefaultWebSecurityManager securityManage = new DefaultWebSecurityManager();
        securityManage.setRealm(shiroRealm);

        //记住我
//        securityManage.setRememberMeManager(rememberMeManager());

        securityManage.setSessionManager(sessionManager());

        securityManage.setCacheManager(redisCacheManager());
//        securityManage.setCacheManager(new MyRedisCacheManager());
        return securityManage;

    }

    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setName("shiroRealm");
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        shiroRealm.setCachingEnabled(true);
        shiroRealm.setAuthorizationCachingEnabled(true);
        shiroRealm.setAuthorizationCacheName("authorizationCache");
        return shiroRealm;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(SystemUtil.HASH_ALGORITHM_NAME);
        credentialsMatcher.setHashIterations(Integer.valueOf(SystemUtil.HASH_ITERATIONS));
        credentialsMatcher.setStoredCredentialsHexEncoded(true);

        return credentialsMatcher;
    }

    @Bean
    public KickoutSessionControlFilter kickoutSessionControlFilter(){
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        kickoutSessionControlFilter.setSessionManager(sessionManager());
        kickoutSessionControlFilter.setRedisUtil(redisUtil);

        kickoutSessionControlFilter.setKickoutAfter(false);
        kickoutSessionControlFilter.setKickoutUrl("/login");
        kickoutSessionControlFilter.setMaxSession(1);
        return kickoutSessionControlFilter;
    }

    @Bean
    public SimpleCookie sessionIdCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("sid");

        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        simpleCookie.setMaxAge(-1);
        return simpleCookie;
    }

    @Bean
    public SessionManager sessionManager(){
        DefaultWebSessionManager defaultWebSessionManager = new ShiroWebSessionManager();
        defaultWebSessionManager.setCacheManager(redisCacheManager());
        defaultWebSessionManager.setSessionDAO(redisSessionDao());
        defaultWebSessionManager.setGlobalSessionTimeout(30*60*1000);
        defaultWebSessionManager.setSessionIdCookie(sessionIdCookie());

        //是否开启删除无效的session对象 默认为true
        defaultWebSessionManager.setDeleteInvalidSessions(true);
        //是否开启开启定时调度器检测过期session 默认为true
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间，清理用户直接关闭浏览器造成的孤立会话 默认为1小时
        defaultWebSessionManager.setSessionValidationInterval(60*60*1000);
        //取消url后面的jSessionId
        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);
        return defaultWebSessionManager;
    }

    @Bean
    public CacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setExpire(30*24*60*60);
//        redisCacheManager.setPrincipalIdFieldName("username");
        return redisCacheManager;
    }

    @Bean
    public SessionIdGenerator sessionIdGenerator(){
        return new JavaUuidEditSessionIdGenerator();
    }
    @Bean
    public SessionDAO redisSessionDao(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setKeyPrefix("shiro:session:");
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }
    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        host+=":"+port;
        redisManager.setHost(host);
        redisManager.setPassword(password);
        return redisManager;

    }

    @Bean
    public SimpleCookie rememberCookie(){
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(30*24*60*60);
        return simpleCookie;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setEncryptionCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    /**
     * FormAuthenticationFilter 过滤器 过滤记住我
     *
     * @return
     */
    @Bean
    public FormAuthenticationFilter formAuthenticationFilter() {
        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        //对应前端的checkbox的name = rememberMe
        formAuthenticationFilter.setRememberMeParam("rememberMe");
        return formAuthenticationFilter;
    }

    /**
     * Spring静态注入
     * 让某个方法的某个实例的返回值作为bean的实例
     * @return
     */
    @Bean
    public MethodInvokingFactoryBean getMethodInvokingFactoryBean(){
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(defaultWebSecurityManager(shiroRealm()));
        return factoryBean;
    }

    /**
     * 添加注解支持
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager defaultWebSecurityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(defaultWebSecurityManager);
        return advisor;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
