package com.imooc.service.search;

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
}
