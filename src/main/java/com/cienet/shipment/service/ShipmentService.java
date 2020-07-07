package com.cienet.shipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cienet.shipment.domain.ShipOrder;

/**
 * Service Interface class for managing ship.
 */
public interface ShipmentService extends IService<ShipOrder> {
    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    void split();

    void merge();

    void changeRootQuantity();
}
