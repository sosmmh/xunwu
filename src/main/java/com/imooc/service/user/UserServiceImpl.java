package com.imooc.service.user;

import com.google.common.collect.Lists;
import com.imooc.base.LoginUserUtil;
import com.imooc.entity.Role;
import com.imooc.entity.User;
import com.imooc.repository.RoleRepository;
import com.imooc.repository.UserRepository;
import com.imooc.service.IUserService;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/13 19:19
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Md5PasswordEncoder passwordEncoder;

    @Override
    public User findUserByName(String userName) {
        User user = userRepository.findByName(userName);

        if (user == null) {
            return null;
        }

        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            throw new DisabledException("权限非法");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorityList(authorities);

        return user;
    }

    @Override
    public ServiceResult<UserDTO> findById(Long userId) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            return ServiceResult.notFound();
        }
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ServiceResult.of(userDTO);
    }

    @Override
    public User findUserByTelephone(String telephone) {
        User user = userRepository.findUserByPhoneNumber(telephone);
        if (user == null) {
            return null;
        }
        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            throw new DisabledException("权限非法");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorityList(authorities);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User addUserByPhone(String telephone) {
        User user = new User();
        user.setPhoneNumber(telephone);
//        user.setName(telephone.substring(0, 3) + "****" + telephone.substring(7, telephone.length()));
        user.setName(telephone);
        Date now = new Date();
        user.setCreateTime(now);
        user.setLastLoginTime(now);
        user.setLastUpdateTime(now);
        userRepository.save(user);

        Role role = new Role();
        role.setName("USER");
        role.setUserId(user.getId());
        roleRepository.save(role);
        user.setAuthorityList(Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER")));
        return user;
    }

    @Override
    @Transactional
    public ServiceResult modifyUserProfile(String profile, String value) {

        Long userId = LoginUserUtil.getLoginUserId();
        if (profile == null || profile.isEmpty()) {
            return new ServiceResult(false, "属性不能为空");
        }
        switch (profile) {
            case "name":
                userRepository.updateName(userId, value);
                break;
            case "email":
                userRepository.updateEmail(userId, value);
                break;
            case "password":
                userRepository.updatePassword(userId, passwordEncoder.encodePassword(value, userId));
                break;
            default:
                    return new ServiceResult(false, "不支持的属性");
        }
        return ServiceResult.success();
    }
}
