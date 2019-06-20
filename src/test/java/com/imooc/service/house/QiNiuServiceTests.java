package com.imooc.service.house;

import com.imooc.ApplicationTests;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.AssertTrue;
import java.io.File;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/15 17:35
 */
public class QiNiuServiceTests extends ApplicationTests {

    @Autowired
    private IQiNiuService qiNiuService;



    @Test
    public void testUploadFile(){
        String fileName = "/Users/lixiahan/IdeaProjects/person/imooc/xunwu-project/tmp/9f8eb819c3bd4f8780895a169242ae33(54512-2017-12-28).jpg";
        File file = new File(fileName);

        Assert.assertTrue(file.exists());

        try {
            Response response = qiNiuService.uploadFile(file);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testDelete() {
        String key = "";
        try {
            Response response = qiNiuService.delete(key);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }
}
