package com.imooc.repository;

import com.imooc.entity.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @description: 角色数据DAO
 * @author: lixiahan
 * @create: 2019/06/13 19:27
 */
public interface HousePictureRepository extends CrudRepository<HousePicture, Long> {

    List<HousePicture> findAllByHouseId(Long houseId);
}
