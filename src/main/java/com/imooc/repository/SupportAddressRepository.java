package com.imooc.repository;

import com.imooc.entity.SupportAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 00:44
 */
public interface SupportAddressRepository extends CrudRepository<SupportAddress, Long> {

    /**
     * 获取有所对应的行政级别
     * @param level
     * @return
     */
    List<SupportAddress> findAllByLevel(String level);

    SupportAddress findByEnNameAndLevel(String enName, String level);

    SupportAddress findByEnNameAndBelongTo(String enName, String belongTo);

    List<SupportAddress> findAllByLevelAndBelongTo(String level, String belongTo);

}
