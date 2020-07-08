package com.cienet.shipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cienet.shipment.domain.ShipBatch;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.vo.Paging;
import com.cienet.shipment.vo.ShipOrderQueryVo;
import com.cienet.shipment.vo.param.ShipOrderParam;

import java.io.Serializable;
import java.util.List;

/**
 * Service Interface class for managing ship.
 */
public interface OrderService extends IService<ShipOrder> {
    /**
     * 保存
     *
     * @param shipOrder
     * @return
     * @throws Exception
     */
    boolean saveOrder(ShipOrder shipOrder) throws Exception;

    /**
     * 修改
     *
     * @param shipOrder
     * @return
     * @throws Exception
     */
    boolean updateOrder(ShipOrder shipOrder) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteOrder(Long id) throws Exception;

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    ShipOrderQueryVo getOrderById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     *
     * @param orderQueryParam
     * @return
     * @throws Exception
     */
    Paging<ShipOrderQueryVo> getOrderPageList(ShipOrderParam orderQueryParam) throws Exception;

    /**
     * @return List of batch
     * @Param id order
     * @Param batchSize
     */
    List<ShipBatch> split(Long id, Double[] quantities) throws Exception;

    List<ShipBatch> merge(Long id, Long[] batchIds) throws Exception;

    List<ShipBatch> changeOrderQuantity(Long id, Double q) throws Exception;
}
