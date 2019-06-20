package com.imooc.base;

import lombok.Data;

/**
 * @description: dataTables响应结构
 * @author: lixiahan
 * @create: 2019/06/16 19:58
 */
@Data
public class ApiDataTableResponse extends ApiResponse {

    private int draw;

    private long recordsTotal;

    private long recordsFiltered;

    public ApiDataTableResponse(ApiResponse.Status status) {
        this(status.getCode(), status.getStandardMessage(), null);
    }

    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);

    }
}
