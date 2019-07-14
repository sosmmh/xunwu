package com.imooc.service.search;

import com.imooc.ApplicationTests;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.form.RentSearch;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

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

}
