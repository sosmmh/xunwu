package com.imooc.repository;

import com.imooc.entity.House;
import com.imooc.entity.Role;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @description: 角色数据DAO
 * @author: lixiahan
 * @create: 2019/06/13 19:27
 */
public interface HouseRepository extends PagingAndSortingRepository<House, Long>, JpaSpecificationExecutor<House> {

    /**
     * 修改
     * @param id
     * @param cover
     */
    @Modifying
    @Query(value = "UPDATE House as house set house.cover = :cover where house.id = :id", nativeQuery = true)
    void updateCover(@Param(value = "id") Long id, @Param(value = "") String cover);
}
