package com.imooc.service.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: 百度位置信息
 * @author: lixiahan
 * @create: 2019/07/14 15:38
 */
@Data
public class BaiduMapLocation {

    @JsonProperty("lon")
    private double longitude;

    @JsonProperty("lat")
    private double latitude;


}
