package com.imooc.web.controller.house;

import com.imooc.base.ApiResponse;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.house.IAddressService;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 00:39
 */
@Controller
public class HouseController {

    @Autowired
    private IAddressService addressService;

    @GetMapping("/address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities() {
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUNT);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("/address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(value = "city_name") String cityEnName) {
        ServiceMultiResult result = addressService.findAllRegionsByCityName(cityEnName);
        if (result.getResultSize() == 0) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUNT);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("/address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(value = "city_name") String cityEnName) {
        List<SubwayDTO> result = addressService.findAllSubwayByCity(cityEnName);
        if (result.isEmpty()) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUNT);
        }
        return ApiResponse.ofSuccess(result);
    }

    @GetMapping("/address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(value = "subway_id") Long subwayId) {
        List<SubwayStationDTO> result = addressService.findAllStationBySubway(subwayId);
        if (result.isEmpty()) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUNT);
        }
        return ApiResponse.ofSuccess(result);
    }
}
