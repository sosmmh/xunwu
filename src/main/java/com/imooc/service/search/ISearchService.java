package com.imooc.service.search;

import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.form.RentSearch;

import java.util.List;

/**
 * @description: 检索接口
 * @author: lixiahan
 * @create: 2019/07/09 22:51
 */
public interface ISearchService {

    /**
     * 索引目标房源
     * @param houseId
     */
    void index(Long houseId);

    /**
     * 移除房源索引
     * @param houseId
     */
    void remove(Long houseId);

    /**
     *
     * @return
     */
    ServiceMultiResult<Long> query(RentSearch rentSearch);

    /**
     * 获取自动补全
     * @return
     */
    ServiceResult<List<String>> suggest(String prefix);

    /**
     * 获取小区房源出租数
     * @param cityEnName
     * @param regionEnName
     * @param district
     * @return
     */
    ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district);
}
