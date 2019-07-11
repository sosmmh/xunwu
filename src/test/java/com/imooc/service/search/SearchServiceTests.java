package com.imooc.service.search;

import com.imooc.ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
