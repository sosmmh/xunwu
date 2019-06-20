package com.imooc.base;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/20 17:56
 */
public class HouseOperation {

    /**
     * 通过审核
     */
    public static final int PASS = 1;

    /**
     * 下架。重新审核
     */
    public static final int PULL_OUT = 2;

    /**
     * 逻辑删除
     */
    public static final int DELETE = 3;

    /**
     * 出租
     */
    public static final int RENT = 4;
}
