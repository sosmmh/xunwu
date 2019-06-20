package com.imooc.entity;

import com.imooc.ApplicationTests;
import com.imooc.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/13 15:52
 */
public class UserRepositoryTest extends ApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindOne() {
        User user = userRepository.findOne(1L);
        Assert.assertEquals("waliwali", user.getName());
    }
}
