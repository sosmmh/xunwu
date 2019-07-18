package com.imooc.base;

import lombok.Getter;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/18 15:46
 */
@Getter
public enum HouseSubscribeStatus {

    // 未预约
    NO_SUBSCRIBE(0),
    // 已加入预约清淡
    IN_ORDER_LIST(1),
    // 已预约看房时间
    IN_ORDER_TIME(2),
    FINISH(3),
    ;

    private int value;

    HouseSubscribeStatus(int value) {
        this.value = value;
    }

    public static HouseSubscribeStatus of(int value) {
        for (HouseSubscribeStatus status : HouseSubscribeStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return HouseSubscribeStatus.NO_SUBSCRIBE;
    }
}
