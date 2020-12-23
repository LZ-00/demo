package com.lz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lz.dto.result.R;
import com.lz.entity.User;
import com.lz.realm.ShiroRealm;
import com.lz.service.UserService;
import io.swagger.annotations.Api;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lz
 * @since 2020-12-09
 */
@RestController
@RequestMapping("/user")
@Api(tags = "User管理")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/view")
    @RequiresPermissions(value = {"user:view"})
    public R view() {
        /*Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        session.setAttribute("message", "hello");*/
        userService.getByUserName("test");
        return R.ok();
    }

    @GetMapping("/getAll")
//    @Cacheable(value = "userInfo",key = "'allUserInfo'")
    public R all(){
        List<User> list = userService.list();
        return R.ok().data("items",list);
    }

    @GetMapping("/updateByID")
//    @CacheEvict(value = "userInfo",allEntries = true)
    public R update(){
        User user = userService.getById(10);
        user.setUsername("pp");
        boolean b = userService.updateById(user);

        return R.ok().data("user",user).message(String.valueOf(b));
    }

    @GetMapping("/addPrem")
    public R addPermission() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username","test");
        User userInfo = userService.getOne(queryWrapper);

        userInfo.setPerms("user:view");

        userService.updateById(userInfo);

        //添加成功之后 清除缓存
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager)SecurityUtils.getSecurityManager();
        ShiroRealm shiroRealm ;
        Collection<Realm> realms = securityManager.getRealms();

        List<Realm> shiroRealms = realms.stream().filter(realm -> !"shiroRealm".equals(realm.getName())).collect(Collectors.toList());
        if (shiroRealms == null) {
            throw new RuntimeException("shiroRealm name error");
        }else {
            shiroRealm = (ShiroRealm) shiroRealms.get(0);
        }
        //清除权限 相关的缓存
        Subject subject = SecurityUtils.getSubject();
        shiroRealm.clearCachedAuthorizationInfo(subject.getPrincipal());
        return R.ok();

    }


    
}

