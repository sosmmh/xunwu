package com.imooc.base;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.Map;

/**
 * @description: 带区间的常用数值定义
 * @author: lixiahan
 * @create: 2019/06/20 23:14
 */
@Data
public class RentValueBlock {

    /**
     * 价格区间
     */
    public static final Map<String, RentValueBlock> PRICE_BLOCK;

    /**
     * 面积区间
     */
    public static final Map<String, RentValueBlock> AREA_BLOCK;

    /**
     * 无限制区间
     */
    public static final RentValueBlock ALL = new RentValueBlock("*", -1, -1);

    static {
        PRICE_BLOCK = ImmutableMap.<String, RentValueBlock>builder()
                .put("*-1000", new RentValueBlock("*-1000", -1, 1000))
                .put("1000-3000", new RentValueBlock("1000-3000", 1000, 3000))
                .put("3000-*", new RentValueBlock("3000-*", 3000, -1))
                .build();

        AREA_BLOCK = ImmutableMap.<String, RentValueBlock>builder()
                .put("*-30", new RentValueBlock("*-30", -1, 30))
                .put("30-50", new RentValueBlock("30-50", 30, 50))
                .put("50-*", new RentValueBlock("50-*", 50, -1))
                .build();
    }

    private String key;
    private int min;
    private int max;

    public RentValueBlock(String key, int min, int max) {
        this.key = key;
        this.min = min;
        this.max = max;
    }

    public static RentValueBlock matchPrice(String key) {
        RentValueBlock rentValueBlock = PRICE_BLOCK.get(key);
        if (rentValueBlock == null) {
            return ALL;
        }
        return rentValueBlock;
    }

    public static RentValueBlock matchArea(String key) {
        RentValueBlock rentValueBlock = AREA_BLOCK.get(key);
        if (rentValueBlock == null) {
            return ALL;
        }
        return rentValueBlock;
    }
}
