package com.imooc.service;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 00:55
 */
@Data
public class ServiceMultiResult<T> {

    private long total;
    private List<T> result;

    public ServiceMultiResult(long total, List<T> result) {
        this.total = total;
        this.result = result;
    }

    public int getResultSize() {
        return this.result == null ? 0 : this.result.size();
    }
}
