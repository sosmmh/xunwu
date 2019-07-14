package com.imooc.service.search;

import lombok.Data;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/13 13:50
 */
@Data
public class HouseSuggest {

    private String input;

    /**
     * 默认权重
     */
    private int weight = 10;
}
