package com.cienet.shipment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cienet.shipment.domain.ShipBatch;
import com.cienet.shipment.repository.ShipBatchRepo;
import com.cienet.shipment.service.ShipBatchService;
import com.cienet.shipment.vo.Paging;
import com.cienet.shipment.vo.ShipBatchQueryVo;
import com.cienet.shipment.vo.param.ShipBatchParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * Service class for managing ship batch.
 */
@Service
@Transactional
public class ShipBatchServiceImpl extends BaseServiceImpl<ShipBatchRepo, ShipBatch> implements ShipBatchService {

    private final Logger log = LoggerFactory.getLogger(ShipBatchServiceImpl.class);

    @Autowired
    private ShipBatchRepo shipBatchRepo;

    @Override
    public boolean saveShipBatch(ShipBatch shipBatch) throws Exception {
        return super.save(shipBatch);
    }

    @Override
    public boolean updateShipBatch(ShipBatch shipBatch) throws Exception {
        return super.updateById(shipBatch);
    }

    @Override
    public boolean deleteShipBatch(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public ShipBatchQueryVo getShipBatchById(Serializable id) throws Exception {
        return shipBatchRepo.getShipBatchById(id);
    }

    @Override
    public Paging<ShipBatchQueryVo> getShipBatchPageList(ShipBatchParam ShipBatchQueryParam) throws Exception {
        Page page = setPageParam(ShipBatchQueryParam, OrderItem.desc("create_time"));
        IPage<ShipBatchQueryVo> iPage = shipBatchRepo.getShipBatchPageList(page, ShipBatchQueryParam);
        return new Paging(iPage);
    }

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
