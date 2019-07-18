package com.imooc.service;

/**
 * @description: 验证码服务
 * @author: lixiahan
 * @create: 2019/07/16 16:31
 */
public interface ISmsService {

    /**
     * 发送验证码到指定手机，并缓存验证码10分钟，并且请求间隔时间1分钟
     * @param telephone
     * @return
     */
    ServiceResult<String> sendSms(String telephone);

    /**
     * 获取缓存中的验证码
     * @param telephone
     * @return
     */
    String getSmsCode(String telephone);

    /**
     * 移除指定手机号的验证码缓存
     */
    void remove(String telephone);
}
