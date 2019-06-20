package com.imooc.web.dto;

import lombok.Data;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 16:34
 */
@Data
public class HouseDetailDTO {

    private String description;

    private String layoutDesc;

    private String traffic;

    private String roundService;

    private int rentWay;

    private Long adminId;

    private String address;

    private Long subwayLineId;

    private Long subwayStationId;

    private String subwayLineName;

    private String subwayStationName;

}
