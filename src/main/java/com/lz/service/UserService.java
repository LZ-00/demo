package com.lz.service;

import com.lz.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lz
 * @since 2020-12-09
 */
public interface UserService extends IService<User> {

    /**
     *  获取用户信息
     * @param userName 用户名
     * @return 该用户名对应的User对象
     */
    User getByUserName(String userName);

}
