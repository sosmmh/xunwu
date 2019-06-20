package com.imooc.web.dto;

import lombok.Data;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/15 17:50
 */
@Data
public final class QiNiuPutRet {

    public String key;
    public String hash;
    public String bucket;
    public int width;
    public int height;

}
