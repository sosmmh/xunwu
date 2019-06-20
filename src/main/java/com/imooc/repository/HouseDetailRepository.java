package com.imooc.repository;

import com.imooc.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

/**
 * @description: 角色数据DAO
 * @author: lixiahan
 * @create: 2019/06/13 19:27
 */
public interface HouseDetailRepository extends CrudRepository<HouseDetail, Long> {

    HouseDetail findByHouseId(Long houseId);
}
