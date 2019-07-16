package com.imooc.service.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/14 14:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseBucketDTO {

    private String key;

    private Long count;
}
