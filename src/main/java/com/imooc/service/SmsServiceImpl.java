package com.imooc.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/16 16:38
 */
@Slf4j
@Service
public class SmsServiceImpl implements ISmsService, InitializingBean {

    @Value("${aliyun.sms.accessKey}")
    private String accessKey;

    @Value("${aliyun.sms.accessKeySecret}")
    private String secretKey;

    @Value("${aliyun.sms.template.code}")
    private String templateCode;

    private static final String SMS_CODE_CONTENT_PREFIX = "SMS::CODE::CONTENT::";
    private static final String SMS_CODE_INTERVAL_PREFIX = "SMS::CODE::INTERVAL::";

    private IAcsClient acsClient;

    private static final String[] NUMS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final Random random = new Random();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public ServiceResult<String> sendSms(String telephone) {

        // 设置时间间隔
        String gapKey = SMS_CODE_INTERVAL_PREFIX + telephone;
        String result = redisTemplate.opsForValue().get(gapKey);
        if (result != null) {
            return new ServiceResult<>(false, "请求次数太频繁");
        }

        String code = generateRandomSmsCode();
        String templateParam = String.format("{\"code\": \"%s\"}", code);

        SendSmsRequest request = new SendSmsRequest();
        // 使用post提交
        request.setMethod(MethodType.POST);
        request.setPhoneNumbers(telephone);
        request.setSignName("xunwu");
        request.setTemplateParam(templateParam);
        request.setTemplateCode(templateCode);

        boolean success = false;
        try {
            SendSmsResponse response = acsClient.getAcsResponse(request);
            if ("OK".equals(response.getCode())) {
                success = true;
            } else {
                log.error("Send Sms not ok for {}", response.getMessage());
                // TODO log this question
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
//        boolean success = true;
        if (success) {
            redisTemplate.opsForValue().set(gapKey, code, 60, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(SMS_CODE_CONTENT_PREFIX + telephone, code, 10, TimeUnit.MINUTES);
            return ServiceResult.of(code);
        } else {
            return new ServiceResult<>(false, "服务忙，请稍后重试");
        }
    }

    @Override
    public String getSmsCode(String telephone) {
        return this.redisTemplate.opsForValue().get(SMS_CODE_CONTENT_PREFIX + telephone);
    }

    @Override
    public void remove(String telephone) {
        this.redisTemplate.delete(SMS_CODE_CONTENT_PREFIX + telephone);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 设置超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, secretKey);

        String product = "Dysmsapi";
        String domain = "dysmsapi.aliyuncs.com";

        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        this.acsClient = new DefaultAcsClient(profile);

    }

    /**
     * 6位验证码生成器
     * @return
     */
    private static String generateRandomSmsCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(10);
            sb.append(NUMS[index]);
        }
        return sb.toString();
//        return "123456";
    }
}
