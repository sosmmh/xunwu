package com.imooc.repository;

import com.imooc.entity.Subway;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 16:04
 */
public interface SubwayRepository extends CrudRepository<Subway, Long> {
    List<Subway> findAllByCityEnName(String cityEnName);
}
