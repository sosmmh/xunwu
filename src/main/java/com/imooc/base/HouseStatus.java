package com.imooc.base;

import lombok.Getter;

/**
 * @description: 房源状态
 * @author: lixiahan
 * @create: 2019/06/16 21:02
 */
@Getter
public enum  HouseStatus {

    NOT_AUDITED(0),
    PASSES(1),
    RENTED(2),
    DELETED(3);

    private int value;

    HouseStatus(int value) {
        this.value = value;
    }
}
