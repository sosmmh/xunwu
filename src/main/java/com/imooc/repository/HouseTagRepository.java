package com.imooc.repository;

import com.imooc.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @description: 角色数据DAO
 * @author: lixiahan
 * @create: 2019/06/13 19:27
 */
public interface HouseTagRepository extends CrudRepository<HouseTag, Long> {

    /**
     * 根据houseId查询所有标签
     * @param id
     * @return
     */
    List<HouseTag> findAllByHouseId(Long id);

    /**
     * 根据houseId和标签名查找
     * @param houseId
     * @param name
     * @return
     */
    HouseTag findByHouseIdAndName(Long houseId, String name);

    /**
     * 根据House查询所有的tag
     * @param houseIds
     * @return
     */
    List<HouseTag> findAllByHouseIdIn(List<Long> houseIds);
}
