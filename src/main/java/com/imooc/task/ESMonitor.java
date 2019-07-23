package com.imooc.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/21 16:01
 */
@Component
@Slf4j
public class ESMonitor {

    private static final String HEALTH_CHECK_API =
            "http://127.0.0.1:9200/_cluster/health";

    private static final String GREEN = "green";

    private static final String YELLOW = "yellow";

    private static final String RED = "red";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JavaMailSender mailSender;

//    @Scheduled(fixedDelay = 5000)
    public void healthCheck() {
        HttpClient httpClient = HttpClients.createDefault();

        HttpGet get = new HttpGet(HEALTH_CHECK_API);

        try {
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
                log.error("Can not access ES Service normally! Please check the server.");
            } else {
                String body = EntityUtils.toString(response.getEntity(), "UTF-8");
                JsonNode jsonNode = objectMapper.readTree(body);
                String status = jsonNode.get("status").asText();

                String message = null;
                boolean isNormal = false;
                switch (status) {
                    case GREEN:
                        message = "ES server run normally.";
                        isNormal = true;
                        break;
                    case YELLOW:
                        message = "ES server status get yellow! Please check the ES Server!";
                        break;
                    case RED:
                        message = "ES server status get RED!!! Must check the ES Server!!!";
                        break;
                    default:
                        message = "Unknown ES status from ES Server for status: " + status;
                }
                if (!isNormal) {
                    sendAlertMessage(message);
                }

                int totalNodes = jsonNode.get("number_of_nodes").asInt();
                if (totalNodes < 5) {
                    sendAlertMessage("ES部分节点丢失:节点少于5");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAlertMessage(String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("670759953@qq.com");
        mailMessage.setTo("13286802211@163.com");
        mailMessage.setSubject("【警告】ES服务监控");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}
