package com.imooc.service.search;

import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.form.MapSearch;
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

    /**
     * 聚合城市数据
     * @param cityEnName
     * @return
     */
    ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName);


    /**
     * 城市级别查询
     * @param cityEnName
     * @param orderBy
     * @param orderDirection
     * @param start
     * @param size
     * @return
     */
    ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size);

    /**
     * 精确范围数据查询
     * @param mapSearch
     * @return
     */
    ServiceMultiResult<Long> mapQuery(MapSearch mapSearch);

}
