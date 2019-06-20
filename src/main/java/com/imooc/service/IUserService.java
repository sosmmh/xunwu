package com.imooc.service;

import com.imooc.entity.User;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/13 19:18
 */
public interface IUserService {
    User findUserByName(String userName);
}
