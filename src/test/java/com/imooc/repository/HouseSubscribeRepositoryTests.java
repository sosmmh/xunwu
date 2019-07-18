package com.imooc.repository;
import java.util.Date;

import com.imooc.ApplicationTests;
import com.imooc.entity.HouseSubscribe;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/18 21:33
 */
public class HouseSubscribeRepositoryTests extends ApplicationTests {

    @Autowired
    private HouseSubscribeRepository houseSubscribeRepository;

    @Test
    public void testSave() {
        HouseSubscribe houseSubscribe = new HouseSubscribe();
        houseSubscribe.setId(0L);
        houseSubscribe.setHouseId(0L);
        houseSubscribe.setUserId(0L);
        houseSubscribe.setAdminId(0L);
        houseSubscribe.setStatus(0);
        houseSubscribe.setCreateTime(new Date());
        houseSubscribe.setLastUpdateTime(new Date());
        houseSubscribe.setOrderTime(new Date());
        houseSubscribe.setTelephone("");
        houseSubscribe.setDescription("");
        HouseSubscribe save = houseSubscribeRepository.save(houseSubscribe);
        Assert.assertNotNull(save);

    }

}