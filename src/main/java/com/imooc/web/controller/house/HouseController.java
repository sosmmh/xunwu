package com.imooc.web.controller.house;

import com.imooc.base.ApiResponse;
import com.imooc.base.RentValueBlock;
import com.imooc.entity.SupportAddress;
import com.imooc.service.IUserService;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.house.IHouseService;
import com.imooc.service.search.HouseBucketDTO;
import com.imooc.service.search.ISearchService;
import com.imooc.web.dto.*;
import com.imooc.web.form.MapSearch;
import com.imooc.web.form.RentSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 00:39
 */
@Controller
public class HouseController {

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private ISearchService searchService;


    @GetMapping("/rent/house/autocomplete")
    @ResponseBody
    public ApiResponse autoComplete(@RequestParam(value = "prefix") String prefix) {

        if (prefix.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult<List<String>> result = searchService.suggest(prefix);

        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("/address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities() {
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("/address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(value = "city_name") String cityEnName) {
        ServiceMultiResult result = addressService.findAllRegionsByCityName(cityEnName);
        if (result.getResultSize() == 0) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("/address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(value = "city_name") String cityEnName) {
        List<SubwayDTO> result = addressService.findAllSubwayByCity(cityEnName);
        if (result.isEmpty()) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result);
    }

    @GetMapping("/address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(value = "subway_id") Long subwayId) {
        List<SubwayStationDTO> result = addressService.findAllStationBySubway(subwayId);
        if (result.isEmpty()) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result);
    }

    @GetMapping("rent/house")
    public String rentHousePage(@ModelAttribute RentSearch rentSearch,
                                Model model, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (rentSearch.getCityEnName() == null) {
            String cityEnNameInSession = (String) session.getAttribute("cityEnName");
            if (cityEnNameInSession == null) {
                redirectAttributes.addAttribute("msg", "must_chose_city");
                return "redirect:/index";
            } else {
                rentSearch.setCityEnName(cityEnNameInSession);
            }
        } else {
            session.setAttribute("cityEnName", rentSearch.getCityEnName());
        }

        ServiceResult<SupportAddressDTO> city = addressService.findCity(rentSearch.getCityEnName());
        if (!city.isSuccess()) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        }
        model.addAttribute("currentCity", city.getResult());

        ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(rentSearch.getCityEnName());
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        }

        ServiceMultiResult<HouseDTO> serviceMultiResult = houseService.query(rentSearch);

        model.addAttribute("total", serviceMultiResult.getTotal());
        model.addAttribute("houses", serviceMultiResult.getResult());

        if (rentSearch.getRegionEnName() == null) {
            rentSearch.setRegionEnName("*");
        }

        model.addAttribute("searchBody", rentSearch);
        model.addAttribute("regions", addressResult.getResult());

        model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
        model.addAttribute("areaBlocks", RentValueBlock.AREA_BLOCK);

        model.addAttribute("currentPriceBlock", RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock", RentValueBlock.matchArea(rentSearch.getAreaBlock()));

        return "rent-list";
    }

    @GetMapping("rent/house/show/{id}")
    public String show(@PathVariable(value = "id") Long houseId,
                       Model model) {
        if (houseId <= 0) {
            return "404";
        }

        ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(houseId);
        if (!serviceResult.isSuccess()) {
            return "404";
        }

        HouseDTO houseDTO = serviceResult.getResult();
        Map<SupportAddress.Level, SupportAddressDTO>
                addressMap = addressService.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());

        SupportAddressDTO city = addressMap.get(SupportAddress.Level.CITY);
        SupportAddressDTO region = addressMap.get(SupportAddress.Level.REGION);

        model.addAttribute("city", city);
        model.addAttribute("region", region);

        ServiceResult<UserDTO> userDTOServiceResult = userService.findById(houseDTO.getAdminId());
        model.addAttribute("agent", userDTOServiceResult.getResult());
        model.addAttribute("house", houseDTO);

        // 多少套出租中
        ServiceResult<Long> aggResult = searchService.aggregateDistrictHouse(city.getEnName(), region.getEnName(), houseDTO.getDistrict());
        model.addAttribute("houseCountInDistrict", aggResult.getResult());

        return "house-detail";
    }

    @GetMapping("rent/house/map")
    public String rentMapPage(@RequestParam(value = "cityEnName") String cityEnName,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        ServiceResult<SupportAddressDTO> city = addressService.findCity(cityEnName);
        if (!city.isSuccess()) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        } else {
            session.setAttribute("cityName", cityEnName);
            model.addAttribute("city", city.getResult());
        }

        ServiceMultiResult<SupportAddressDTO> regions = addressService.findAllRegionsByCityName(cityEnName);

        ServiceMultiResult<HouseBucketDTO> serviceResult = searchService.mapAggregate(cityEnName);
        model.addAttribute("aggData", serviceResult.getResult());
        model.addAttribute("total", serviceResult.getTotal());
        model.addAttribute("regions", regions.getResult());

        return "rent-map";
    }

    @GetMapping("rent/house/map/houses")
    @ResponseBody
    public ApiResponse rentMapHouses(@ModelAttribute MapSearch mapSearch) {
        if (mapSearch.getCityEnName() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须选择城市");
        }

        ServiceMultiResult<HouseDTO> serviceMultiResult;
        if (mapSearch.getLevel() < 13) {
            serviceMultiResult = houseService.wholeMapQuery(mapSearch);
        } else {
            // 小地图查询必须要传递地图边界
            serviceMultiResult = houseService.boundMapQuery(mapSearch);
        }

        ApiResponse response = ApiResponse.ofSuccess(serviceMultiResult.getResult());
        response.setMore(serviceMultiResult.getTotal() >  (mapSearch.getStart() + mapSearch.getSize()));
        return response;
    }
}
