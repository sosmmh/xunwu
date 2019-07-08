package com.imooc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 00:48
 */
@Data
public class SupportAddressDTO {

    private Long id;

    @JsonProperty(value = "belong_to")
    private String belongTo;

    @JsonProperty(value = "en_name")
    private String enName;

    @JsonProperty(value= "cn_name")
    private String cnName;

    private String level;
}
