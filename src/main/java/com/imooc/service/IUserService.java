package com.imooc.service;

import com.imooc.entity.User;
import com.imooc.web.dto.UserDTO;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/13 19:18
 */
public interface IUserService {

    User findUserByName(String userName);

    ServiceResult<UserDTO> findById(Long userId);
}
