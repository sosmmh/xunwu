package com.imooc.service.house;

import com.google.common.collect.Maps;
import com.imooc.base.HouseSort;
import com.imooc.base.HouseStatus;
import com.imooc.base.HouseSubscribeStatus;
import com.imooc.base.LoginUserUtil;
import com.imooc.entity.*;
import com.imooc.repository.*;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.search.ISearchService;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.HouseDetailDTO;
import com.imooc.web.dto.HousePictureDTO;
import com.imooc.web.dto.HouseSubscribeDTO;
import com.imooc.web.form.*;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/06/16 16:50
 */
@Service
public class HouseServiceImpl implements IHouseService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private HouseSubscribeRepository houseSubscribeRepository;

    @Autowired
    private IQiNiuService qiNiuService;

    @Autowired
    private ISearchService searchService;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        HouseDetail houseDetail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidationResult = wrapperDetailInfo(houseDetail, houseForm);
        if (subwayValidationResult != null) {
            return subwayValidationResult;
        }

        House house = new House();
        modelMapper.map(houseForm, house);

        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        house = houseRepository.save(house);

        houseDetail.setHouseId(house.getId());
        houseDetail = houseDetailRepository.save(houseDetail);

        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        Iterable<HousePicture> housePictures = housePictureRepository.save(pictures);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);

        List<HousePictureDTO> housePictureDTOList = new ArrayList<>();
        houseDTO.setHouseDetail(houseDetailDTO);
        housePictures.forEach(housePicture -> housePictureDTOList.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(housePictureDTOList);
        houseDTO.setCover(this.cdnPrefix + house.getCover());

        List<String> tags = houseForm.getTags();
        if (tags != null || !tags.isEmpty()) {
            List<HouseTag> houseTagList = new ArrayList<>();
            for (String tag : tags) {
                houseTagList.add(new HouseTag(house.getId(), tag));
            }
            houseTagRepository.save(houseTagList);
            houseDTO.setTags(tags);
        }

        return new ServiceResult<>(true, null, houseDTO);
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DataTableSearch searchBody) {

        List<HouseDTO> houseDTOS = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength();

        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);

        Specification<House> specification = (root, query, cb) -> {
            Predicate predicate = cb.equal(root.get("adminId"), LoginUserUtil.getLoginUserId());
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));
            if (searchBody.getCity() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), searchBody.getCity()));
            }

            if (searchBody.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), searchBody.getStatus()));
            }

            if (searchBody.getCreateTimeMin() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMin()));
            }

            if (searchBody.getCreateTimeMax() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMax()));
            }

            if (searchBody.getTitle() != null) {
                predicate = cb.and(predicate, cb.like(root.get("title"), searchBody.getTitle() + "%"));
            }

            return predicate;
        };

        Page<House> houses = houseRepository.findAll(specification, pageable);
        houses.forEach(e -> {
            HouseDTO houseDTO = modelMapper.map(e, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + e.getCover());
            houseDTOS.add(houseDTO);
        });

        return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Long id) {

        House house = houseRepository.findOne(id);

        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = houseDetailRepository.findByHouseId(id);
        List<HousePicture> pictures = housePictureRepository.findAllByHouseId(id);

        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        pictures.forEach(e -> pictureDTOS.add(modelMapper.map(e, HousePictureDTO.class)));

        List<HouseTag> tags = houseTagRepository.findAllByHouseId(id);
        List<String> tagList = tags.stream().map(e -> e.getName()).collect(Collectors.toList());

        HouseDTO result = modelMapper.map(house, HouseDTO.class);
        result.setHouseDetail(houseDetailDTO);
        result.setPictures(pictureDTOS);
        result.setTags(tagList);

        if (LoginUserUtil.getLoginUserId() > 0) {
            // 已登录用户
            HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndUserId(house.getId(), LoginUserUtil.getLoginUserId());
            if (subscribe != null) {
                result.setSubscribeStatus(subscribe.getStatus());
            }
        }

        return ServiceResult.of(result);
    }

    @Override
    public ServiceResult removePhoto(Long id) {
        HousePicture picture = housePictureRepository.findOne(id);
        if (picture == null) {
            return ServiceResult.notFound();
        }

        try {
            Response response = this.qiNiuService.delete(picture.getPath());
            if (response.isOK()) {
                housePictureRepository.delete(id);
                return ServiceResult.success();
            } else {
                return new ServiceResult<>(false, response.error);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            return new ServiceResult(false, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult updateCover(Long coverId, Long targetId) {
        HousePicture cover = housePictureRepository.findOne(coverId);
        if (cover == null) {
            return ServiceResult.notFound();
        }

        houseRepository.updateCover(targetId, cover.getPath());
        return ServiceResult.success();
    }

    @Override
    public ServiceResult addTag(Long houseId, String tag) {

        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseTag houseTag = new HouseTag(houseId, tag);
        houseTagRepository.save(houseTag);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult removeTag(Long houseId, String tag) {

        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseTag houseTag = houseTagRepository.findByHouseIdAndName(house.getId(), tag);

        houseTagRepository.delete(houseTag.getId());

        return ServiceResult.success();
    }

    @Override
    public ServiceResult update(HouseForm houseForm) {

        House house = houseRepository.findOne(houseForm.getId());
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseForm.getId());
        if (houseDetail == null) {
            return ServiceResult.notFound();
        }

        ServiceResult<HouseDTO> houseDTOServiceResult = wrapperDetailInfo(houseDetail, houseForm);
        if (houseDTOServiceResult != null) {
            return houseDTOServiceResult;
        }

        houseDetailRepository.save(houseDetail);

        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        housePictureRepository.save(pictures);

        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }

        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(new Date());
        houseRepository.save(house);

        if (house.getStatus() == HouseStatus.PASSES.getValue()) {
            searchService.index(house.getId());
        }

        return ServiceResult.success();
    }

    @Override
    public ServiceResult updateStatus(Long id, int status) {

        House house = houseRepository.findOne(id);
        if (house == null) {
            return ServiceResult.notFound();
        }

        if (house.getStatus() == status) {
            return new ServiceResult(false, "状态没有发生变化");
        }

        if (house.getStatus() == HouseStatus.RENTED.getValue()) {
            return new ServiceResult(false, "已出租的房源不允许修改状态");
        }

        if (house.getStatus() == HouseStatus.DELETED.getValue()) {
            return new ServiceResult(false, "已删除的资源不允许操作");
        }

        houseRepository.updateStatus(house.getId(), status);

        // 上架要更新索引，其他情况要删除索引
        if (status == HouseStatus.PASSES.getValue()) {
            searchService.index(house.getId());
        } else {
            searchService.remove(house.getId());
        }

        return ServiceResult.success();
    }

    private List<HouseDTO> wrapperHouseResult(List<Long> houseIds) {
        List<HouseDTO> result = new ArrayList<>();

        Map<Long, HouseDTO> idToHouseMap = new HashMap<>();
        Iterable<House> houses = houseRepository.findAll(houseIds);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            idToHouseMap.put(house.getId(), houseDTO);
        });

        wrapperHouseList(houseIds, idToHouseMap);

        // 矫正顺序
        for (Long houseId : houseIds) {
            result.add(idToHouseMap.get(houseId));
        }

        return result;
    }

    @Override
    public ServiceMultiResult<HouseDTO> query(RentSearch rentSearch) {

        if (rentSearch.elasticSearch()) {
            ServiceMultiResult<Long> serviceResult = searchService.query(rentSearch);
            if (serviceResult.getTotal() == 0) {
                return new ServiceMultiResult<>(0, new ArrayList<>());
            }
            return new ServiceMultiResult<>(serviceResult.getTotal(), wrapperHouseResult(serviceResult.getResult()));
        }

        return simpleQuery(rentSearch);
    }

    @Override
    public ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch) {
        ServiceMultiResult<Long> serviceResult = searchService.mapQuery(mapSearch.getCityEnName(), mapSearch.getOrderBy(),
                mapSearch.getOrderDirection(), mapSearch.getStart(), mapSearch.getSize());

        if (serviceResult.getTotal() == 0) {
            return new ServiceMultiResult<>(0, new ArrayList<>());
        }

        List<HouseDTO> houses = wrapperHouseResult(serviceResult.getResult());

        return new ServiceMultiResult<>(serviceResult.getTotal(), houses);
    }

    @Override
    public ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch) {

        ServiceMultiResult<Long> serviceMultiResult = searchService.mapQuery(mapSearch);
        if (serviceMultiResult.getTotal() == 0) {
            return new ServiceMultiResult<>(0, new ArrayList<>());
        }

        List<HouseDTO> houses = wrapperHouseResult(serviceMultiResult.getResult());

        return new ServiceMultiResult<>(serviceMultiResult.getTotal(), houses);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult addSubscribeOrder(Long houseId) {
        Long userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe != null) {
            return new ServiceResult(false, "已加入预约");
        }

        House house = houseRepository.findOne(houseId);

        subscribe = new HouseSubscribe();
        Date now = new Date();
        subscribe.setCreateTime(now);
        subscribe.setLastUpdateTime(now);
        subscribe.setUserId(userId);
        subscribe.setHouseId(houseId);
        subscribe.setStatus(HouseSubscribeStatus.IN_ORDER_LIST.getValue());
        subscribe.setAdminId(house.getAdminId());
        houseSubscribeRepository.save(subscribe);
        return ServiceResult.success();
    }

    @Override
    public ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatus status, int start, int size) {

        Long userId = LoginUserUtil.getLoginUserId();

        Pageable pageable = new PageRequest(start / size, size, new Sort(Sort.Direction.DESC, "createTime"));

        Page<HouseSubscribe> page = houseSubscribeRepository.findAllByUserIdAndStatus(userId, status.getValue(), pageable);

        return wrapper(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult subscribe(Long houseId, Date orderTime, String telephone, String desc) {

        Long userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe == null) {
            return new ServiceResult(false, "无预约记录");
        }

        if (subscribe.getStatus() != HouseSubscribeStatus.IN_ORDER_LIST.getValue()) {
            return new ServiceResult(false, "无法预约");
        }

        subscribe.setStatus(HouseSubscribeStatus.IN_ORDER_TIME.getValue());
        subscribe.setLastUpdateTime(new Date());
        subscribe.setTelephone(telephone);
        subscribe.setDescription(desc);
        subscribe.setOrderTime(orderTime);
        houseSubscribeRepository.save(subscribe);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult cancelSubscribe(Long houseId) {
        Long userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe == null) {
            return new ServiceResult(false, "无预约记录");
        }

        houseSubscribeRepository.delete(subscribe.getId());

        return ServiceResult.success();
    }

    @Override
    public ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size) {

        Long adminId = LoginUserUtil.getLoginUserId();

        Pageable pageable = new PageRequest(start / size, size, new Sort(Sort.Direction.DESC,  "orderTime"));

        Page<HouseSubscribe> page = houseSubscribeRepository
                .findAllByAdminIdAndStatus(adminId, HouseSubscribeStatus.IN_ORDER_TIME.getValue(), pageable);

        return wrapper(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult finishSubscribe(Long houseId) {

        Long adminId = LoginUserUtil.getLoginUserId();
        HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndAdminId(houseId, adminId);
        if (subscribe == null) {
            return new ServiceResult(false, "无预约记录");
        }

        houseSubscribeRepository.updateStatus(subscribe.getId(), HouseSubscribeStatus.FINISH.getValue());
        houseRepository.updateWatchTimes(houseId);
        return ServiceResult.success();
    }

    private ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> wrapper(Page<HouseSubscribe> page) {
        List<Pair<HouseDTO, HouseSubscribeDTO>> result = new ArrayList<>();

        if (page.getSize() < 1) {
            return new ServiceMultiResult<>(page.getTotalElements(), result);
        }

        List<HouseSubscribeDTO> subscribeDTOS = new ArrayList<>();
        List<Long> houseIds = new ArrayList<>();
        page.forEach(houseSubscribe -> {
            subscribeDTOS.add(modelMapper.map(houseSubscribe, HouseSubscribeDTO.class));
            houseIds.add(houseSubscribe.getHouseId());
        });
        Map<Long, HouseDTO> idToHouseMap = new HashMap<>();
        Iterable<House> houses = houseRepository.findAll(houseIds);
        houses.forEach(house -> {
            idToHouseMap.put(house.getId(), modelMapper.map(house, HouseDTO.class));
        });

        for (HouseSubscribeDTO subscribeDTO : subscribeDTOS) {
            Pair<HouseDTO, HouseSubscribeDTO> pair = Pair.of(idToHouseMap.get(subscribeDTO.getHouseId()), subscribeDTO);
            result.add(pair);
        }
        return new ServiceMultiResult<>(page.getTotalElements(), result);
    }

    private ServiceMultiResult<HouseDTO> simpleQuery(RentSearch rentSearch) {
        Sort sort = HouseSort.generateSort(rentSearch.getOrderBy(), rentSearch.getOrderDirection());
        int page = rentSearch.getStart() / rentSearch.getSize();

        Pageable pageable = new PageRequest(page, rentSearch.getSize(), sort);

        Specification<House> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("status"), HouseStatus.PASSES.getValue());
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("cityEnName"), rentSearch.getCityEnName()));
            if (HouseSort.DISTANCE_TO_SUBWAY_KEY.equals(rentSearch.getOrderBy())) {
                criteriaBuilder.and(predicate,
                        criteriaBuilder.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY_KEY), -1));
            }
            return predicate;
        };


        Page<House> houses = houseRepository.findAll(specification, pageable);
        List<HouseDTO> houseDTOS = new ArrayList<>();

        List<Long> houseIds = new ArrayList<>();
        Map<Long, HouseDTO> idToHouseMap = Maps.newHashMap();
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);

            houseIds.add(house.getId());
            idToHouseMap.put(house.getId(), houseDTO);
        });

        wrapperHouseList(houseIds, idToHouseMap);

        return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
    }

    /**
     *  渲染详细信息 及 标签
     * @param houseIds
     * @param idToHouseMap
     */
    private void wrapperHouseList(List<Long> houseIds, Map<Long, HouseDTO> idToHouseMap) {

        List<HouseDetail> details = houseDetailRepository.findAllByHouseIdIn(houseIds);
        details.forEach(houseDetail -> {
            HouseDTO houseDTO = idToHouseMap.get(houseDetail.getHouseId());
            HouseDetailDTO detailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
            houseDTO.setHouseDetail(detailDTO);
        });

        List<HouseTag> houseTags = houseTagRepository.findAllByHouseIdIn(houseIds);
        houseTags.forEach(houseTag -> {
            HouseDTO houseDTO = idToHouseMap.get(houseTag.getHouseId());
            houseDTO.getTags().add(houseTag.getName());
        });

    }

    private List<HousePicture> generatePictures(HouseForm houseForm, Long houseId) {
        List<HousePicture> pictures = new ArrayList<>();
        if (houseForm.getPhotos() == null) {
            return pictures;
        }

        for (PhotoForm photoForm : houseForm.getPhotos()) {
            HousePicture housePicture = new HousePicture();
            housePicture.setHouseId(houseId);
            housePicture.setCdnPrefix(cdnPrefix);
            housePicture.setPath(photoForm.getPath());
            housePicture.setWidth(photoForm.getWidth());
            housePicture.setHeight(photoForm.getHeight());
            pictures.add(housePicture);
        }
        return pictures;
    }

    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {

        Subway subway = subwayRepository.findOne(houseForm.getSubwayLineId());
        if (subway == null) {
            return new ServiceResult<>(false, "Not valid subway line");
        }

        SubwayStation subwayStation = subwayStationRepository.findOne(houseForm.getSubwayStationId());
        if (subwayStation == null || !subway.getId().equals(subwayStation.getSubwayId())) {
            return new ServiceResult<>(false, "Not valid subway station");
        }

        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());

        houseDetail.setSubwayStationId(subwayStation.getId());
        houseDetail.setSubwayStationName(subwayStation.getName());

        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setDetailAddress(houseForm.getDetailAddress());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
        return null;
    }
}
