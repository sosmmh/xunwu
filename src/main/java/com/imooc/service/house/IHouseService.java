package com.imooc.service.house;

import com.imooc.base.HouseSubscribeStatus;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.HouseSubscribeDTO;
import com.imooc.web.form.DataTableSearch;
import com.imooc.web.form.HouseForm;
import com.imooc.web.form.MapSearch;
import com.imooc.web.form.RentSearch;
import org.springframework.data.util.Pair;

import java.util.Date;

/**
 * @description: 房屋管理服务接口
 * @author: lixiahan
 * @create: 2019/06/16 16:32
 */
public interface IHouseService {

    /**
     * 保存房子信息
     * @param houseForm
     * @return
     */
    ServiceResult<HouseDTO> save(HouseForm houseForm);

    /**
     * 查询房子信息
     * @param searchBody
     * @return
     */
    ServiceMultiResult<HouseDTO> adminQuery(DataTableSearch searchBody);

    /**
     * 查询完整的信息
     * @param id
     * @return
     */
    ServiceResult<HouseDTO> findCompleteOne(Long id);

    /**
     * 移除图片
     * @param id
     * @return
     */
    ServiceResult removePhoto(Long id);

    /**
     * 修改封面接口
     * @param coverId
     * @param targetId
     * @return
     */
    ServiceResult updateCover(Long coverId, Long targetId);

    /**
     * 增加标签接口
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult addTag(Long houseId, String tag);

    /**
     * 移除标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult removeTag(Long houseId, String tag);

    /**
     * 修改房子信息
     * @param houseForm
     * @return
     */
    ServiceResult update(HouseForm houseForm);

    /**
     * 修改房源状态
     * @param id
     * @param status
     * @return
     */
    ServiceResult updateStatus(Long id, int status);


    /**
     * 查询房源信息集
     * @param rentSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);

    /**
     * 全地图查询
     * @param mapSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch);

    /**
     * 精确范围数据查询
     * @param mapSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch);

    /**
     * 加入预约清单
     * @param houseId
     * @return
     */
    ServiceResult addSubscribeOrder(Long houseId);

    /**
     * 获取对应状态的预约列表
     */
    ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatus status, int start, int size);

    /**
     * 预约看房时间
     * @param houseId
     * @param orderTime
     * @param telephone
     * @param desc
     * @return
     */
    ServiceResult subscribe(Long houseId, Date orderTime, String telephone, String desc);

    /**
     * 取消预约看房
     * @param houseId
     * @return
     */
    ServiceResult cancelSubscribe(Long houseId);

    /**
     * 管理员查询预约列表
     * @param start
     * @param size
     * @return
     */
    ServiceMultiResult<Pair<HouseDTO,HouseSubscribeDTO>> findSubscribeList(int start, int size);

    /**
     * 管理员完成预约
     * @param houseId
     * @return
     */
    ServiceResult finishSubscribe(Long houseId);
}
