package com.cienet.shipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cienet.shipment.domain.ShipBatch;
import com.cienet.shipment.vo.Paging;
import com.cienet.shipment.vo.ShipBatchQueryVo;
import com.cienet.shipment.vo.param.ShipBatchParam;

import java.io.Serializable;

/**
 * Service Interface class for managing ship.
 */
public interface ShipBatchService extends IService<ShipBatch> {

    /**
     * 保存
     *
     * @param shipBatch
     * @return
     * @throws Exception
     */
    boolean saveShipBatch(ShipBatch shipBatch) throws Exception;

    /**
     * 修改
     *
     * @param shipBatch
     * @return
     * @throws Exception
     */
    boolean updateShipBatch(ShipBatch shipBatch) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteShipBatch(Long id) throws Exception;

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    ShipBatchQueryVo getShipBatchById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     *
     * @param ShipBatchQueryParam
     * @return
     * @throws Exception
     */
    Paging<ShipBatchQueryVo> getShipBatchPageList(ShipBatchParam ShipBatchQueryParam) throws Exception;

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    void split();

    void merge();

    void changeRootQuantity();
}
