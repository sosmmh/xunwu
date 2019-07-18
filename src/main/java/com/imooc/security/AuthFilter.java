package com.imooc.security;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.imooc.base.LoginUserUtil;
import com.imooc.entity.User;
import com.imooc.service.ISmsService;
import com.imooc.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/16 16:39
 */
public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISmsService smsService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String name = obtainUsername(request);
        if (!Strings.isNullOrEmpty(name)) {
            request.setAttribute("username", name);
            return super.attemptAuthentication(request, response);
        }

        String telephone = request.getParameter("telephone");
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
            throw new BadCredentialsException("Wrong telephone number");
        }

        User user = userService.findUserByTelephone(telephone);
        String inputCode = request.getParameter("smsCode");
        String sessionCode = smsService.getSmsCode(telephone);
        if (Objects.equal(inputCode, sessionCode)) {
            if (user == null) {
                // 如果用户第一次用手机登录 则自动注册该用户
                user = userService.addUserByPhone(telephone);
            }
            smsService.remove(telephone);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } else {
            throw new BadCredentialsException("smsCodeError");
        }
    }
}

