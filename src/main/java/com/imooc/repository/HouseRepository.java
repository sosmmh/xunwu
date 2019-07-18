package com.imooc.repository;

import com.imooc.entity.House;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
    @Query(value = "UPDATE House as house set house.cover = :cover where house.id = :id")
    void updateCover(@Param(value = "id") Long id, @Param(value = "") String cover);

    /**
     * 修改房源状态
     * @param id
     * @param status
     */
    @Modifying
    @Query("update House as house set house.status = :status where house.id = :id")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);

    /**
     * 看房热度+1
     * @param houseId
     */
    @Modifying
    @Query("update House as house set house.watchTimes = house.watchTimes + 1 where house.id = :id")
    void updateWatchTimes(@Param(value = "id") Long houseId);
}
