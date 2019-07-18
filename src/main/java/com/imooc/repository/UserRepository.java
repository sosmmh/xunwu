package com.imooc.repository;

import com.imooc.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/13 15:51
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findByName(String userName);

    User findUserByPhoneNumber(String telephone);

    @Modifying
    @Query("UPDATE User as user set user.name = :name where user.id = :id")
    void updateName(@Param(value = "id") Long id, @Param(value = "name") String name);

    @Modifying
    @Query("UPDATE User as user set user.email = :email where user.id = :id")
    void updateEmail(@Param(value = "id") Long id, @Param(value = "email") String email);

    @Modifying
    @Query("UPDATE User as user set user.password = :password where user.id = :id")
    void updatePassword(@Param(value = "id") Long id, @Param(value = "password") String password);
}
