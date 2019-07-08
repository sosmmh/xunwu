package com.imooc.web.dto;

import lombok.Data;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/08 22:23
 */
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String avatar;
    private String phoneNumber;
    private String lastLoginTime;

}
