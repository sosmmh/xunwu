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

    /**
     * 根据电话号码寻找用户
     * @param telephone
     * @return
     */
    User findUserByTelephone(String telephone);

    /**
     * 通过手机号注册用户
     * @param telephone
     * @return
     */
    User addUserByPhone(String telephone);

    /**
     * 根据指定的属性值修改目标
     * @param profile
     * @param value
     * @return
     */
    ServiceResult modifyUserProfile(String profile, String value);
}
