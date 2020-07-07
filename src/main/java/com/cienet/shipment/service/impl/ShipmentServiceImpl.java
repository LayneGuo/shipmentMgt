package com.cienet.shipment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.repository.ShipOrderRepo;
import com.cienet.shipment.service.ShipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing ship.
 */
@Service
@Transactional
public class ShipmentServiceImpl extends ServiceImpl<ShipOrderRepo, ShipOrder> implements ShipmentService {

    private final Logger log = LoggerFactory.getLogger(ShipmentServiceImpl.class);

    @Override
    public void split() {

    }

    @Override
    public void merge() {

    }

    @Override
    public void changeRootQuantity() {

    }
}
