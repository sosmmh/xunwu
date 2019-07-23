package com.imooc.service.search;

import com.imooc.ApplicationTests;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.form.RentSearch;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/10 21:28
 */
public class SearchServiceTests extends ApplicationTests {

    @Autowired
    private ISearchService searchService;

    @Test
    public void testIndex() {
        Long targetHouseId = 16L;
        searchService.index(targetHouseId);
    }

    @Test
    public void testRemove() {
        Long targetHouseId = 16L;
        searchService.remove(targetHouseId);
    }

    @Test
    public void testQuery() {
        RentSearch rentSearch = new RentSearch();
        rentSearch.setCityEnName("bj");
        rentSearch.setStart(0);
        rentSearch.setSize(10);
        ServiceMultiResult<Long> serviceResult = searchService.query(rentSearch);
        Assert.assertEquals(6, serviceResult.getTotal());
    }

    @Test
    public void testAddAllIndex() {
        List<Long> list = Arrays.asList(16L, 17L, 18L, 19L, 20L, 21L, 24L, 25L, 26L, 27L);
        for (Long id : list) {
            searchService.index(id);
        }
    }

    @Test
    public void testSuggest() {
        String prefix = "大豪";
        ServiceResult<List<String>> result = searchService.suggest(prefix);
        List<String> suggests = result.getResult();
        System.out.println(suggests);
    }

    private static class MyTask extends TimerTask {

        private String name;
        private CountDownLatch latch;

        public MyTask(String name, CountDownLatch latch) {
            this.name = name;
            this.latch = latch;
        }

        @Override
        public void run() {

            System.out.println(name + " 开始时间：" + new Date());
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(name + " 结束时间：" + new Date());
            latch.countDown();
        }
    }

    private static class MyTask2 extends TimerTask {

        private String name;

        public MyTask2(String name) {
            this.name = name;
        }

        @Override
        public void run() {

            System.out.println(name + " 开始时间：" + new Date());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(name + " 结束时间：" + new Date());
        }
    }


    public static void main(String[] args) {

//        CountDownLatch latch = new CountDownLatch(2);
//
//        Timer timer = new Timer();
//
//        MyTask 定时1 = new MyTask("定时1", latch);
//        MyTask 定时2 = new MyTask("定时2", latch);
//        timer.schedule(定时1, 1000);
//        timer.schedule(定时2, 1000);
//
//        try {
//            latch.await();
//            timer.cancel();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        CountDownLatch latch = new CountDownLatch(2);

        Timer timer = new Timer();

        MyTask2 定时1 = new MyTask2("定时1");
        MyTask2 定时2 = new MyTask2("定时2");
        timer.schedule(定时1, 1000);
        timer.schedule(定时2, 1000);

        try {
            latch.await();
            timer.cancel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
