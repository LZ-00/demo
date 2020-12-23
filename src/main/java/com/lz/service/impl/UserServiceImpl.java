package com.lz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.entity.User;
import com.lz.mapper.UserMapper;
import com.lz.service.UserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lz
 * @since 2020-12-09
 */
@Service
@CacheConfig(cacheNames = "common:userInfo")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getByUserName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",userName);
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }

    @Override
    @Cacheable(key = "'allUserInfo'")
    public List<User> list() {
        System.out.println("select from db");
        return super.list();
    }

    @Override
    @CacheEvict(allEntries = true,beforeInvocation = true)
    public boolean updateById(User entity) {
        return super.updateById(entity);
    }
}
