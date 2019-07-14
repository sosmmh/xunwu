package com.imooc.web.form;

import com.imooc.base.RentValueBlock;
import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * @description: 租房请求参数结构体
 * @author: lixiahan
 * @create: 2019/06/20 18:06
 */
@Data
public class RentSearch {

    private String cityEnName;
    private String regionEnName;
    private String priceBlock;
    private String areaBlock;
    private int room;
    private int direction;
    private String keywords;
    private int rentWay = -1;
    private String orderBy = "lastUpdateTime";
    private String orderDirection = "desc";

    private int start = 0;

    private int size = 5;

    public int getSize() {
        if (this.size < 1) {
            return 5;
        } else if (this.size > 100) {
            return 100;
        } else {
            return this.size;
        }
    }

    public int getRentWay() {
        if (rentWay > -2 && rentWay < 2) {
            return rentWay;
        } else {
            return -1;
        }
    }

    public boolean elasticSearch() {
        return !ObjectUtils.isEmpty(keywords) || room > 0 || rentWay > -1 || direction > 0
                || !RentValueBlock.ALL.equals(RentValueBlock.matchArea(areaBlock))
                || !RentValueBlock.ALL.equals(RentValueBlock.matchPrice(priceBlock));
    }

}
