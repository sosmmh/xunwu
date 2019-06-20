package com.imooc.base;

import com.imooc.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 16:53
 */
public class LoginUserUtil {

    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static Long getLoginUserId() {
        User user = load();
        if (user == null) {
            return -1L;
        }
        return user.getId();
    }
}
