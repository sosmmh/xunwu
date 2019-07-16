package com.imooc.web.form;

import lombok.Data;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/14 22:25
 */
@Data
public class MapSearch {

    private String cityEnName;

    /**
     * 地图缩放级别
     */
    private int level = 12;
    private String orderBy = "lastUpdateTime";
    private String orderDirection = "desc";
    /**
     * 左上角
     */
    private Double leftLongitude;
    private Double leftLatitude;

    /**
     * 右下角
     */
    private Double rightLongitude;
    private Double rightLatitude;

    private int start = 0;
    private int size = 5;
}
