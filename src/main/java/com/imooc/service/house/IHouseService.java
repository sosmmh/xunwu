package com.imooc.service.house;

import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.form.DataTableSearch;
import com.imooc.web.form.HouseForm;

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
     * 修改房子信息
     * @param houseForm
     * @return
     */
    ServiceResult update(HouseForm houseForm);

    /**
     * 移除标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult removeTag(Long houseId, String tag);
}
