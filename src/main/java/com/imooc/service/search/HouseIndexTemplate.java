package com.imooc.service.search;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 索引结构模板
 * @author: lixiahan
 * @create: 2019/07/09 22:32
 */
@Data
public class HouseIndexTemplate {
    private Long houseId;

    private String title;

    private int price;

    private int area;

    private Date createTime;

    private Date lastUpdateTime;

    private String cityEnName;

    private String regionEnName;

    private int direction;

    private int distanceToSubway;

    private int room;

    private String subwayLineName;

    private String subwayStationName;

    private String street;

    private String district;

    private String description;

    private String layoutDesc;

    private String traffic;

    private String roundService;

    private int rentWay;

    private List<String> tags;

    private List<HouseSuggest> suggest;

    private BaiduMapLocation location;

}