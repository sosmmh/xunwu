package com.imooc.repository;

import com.imooc.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/13 15:51
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findByName(String userName);
}
