package com.cienet.shipment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.domain.ShipBatch;
import com.cienet.shipment.repository.ShipOrderRepo;
import com.cienet.shipment.service.OrderService;
import com.cienet.shipment.service.ShipBatchService;
import com.cienet.shipment.exception.DaoException;
import com.cienet.shipment.vo.param.ShipOrderParam;
import com.cienet.shipment.vo.Paging;
import com.cienet.shipment.vo.ShipOrderQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service class for managing ship.
 */
@Service
@Transactional
@Slf4j
public class OrderServiceImpl extends BaseServiceImpl<ShipOrderRepo, ShipOrder> implements OrderService {

    @Autowired
    private ShipOrderRepo orderMapper;

    @Autowired
    private ShipBatchService shipBatchService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrder(ShipOrder shipOrder) throws Exception {
        return super.save(shipOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOrder(ShipOrder shipOrder) throws Exception {
        return super.updateById(shipOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteOrder(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public ShipOrderQueryVo getOrderById(Serializable id) throws Exception {
        return orderMapper.getShipOrderById(id);
    }

    @Override
    public Paging<ShipOrderQueryVo> getOrderPageList(ShipOrderParam orderQueryParam) throws Exception {
        Page page = setPageParam(orderQueryParam, OrderItem.desc("create_time"));
        IPage<ShipOrderQueryVo> iPage = orderMapper.getShipOrderPageList(page, orderQueryParam);
        return new Paging(iPage);
    }

    @Override
    public List<ShipBatch> split(Long id, Integer batchSize) throws Exception {
        ShipOrder shipOrder =
            Optional.ofNullable(lambdaQuery().eq(ShipOrder::getId, id).one()).orElseThrow(() -> new DaoException("item not " +
                "found"));

        return splitInternal(shipOrder, batchSize);
    }

    private List<ShipBatch> splitInternal(ShipOrder shipOrder, Integer batchSize) throws Exception {
        if (!shipBatchService.lambdaUpdate().eq(ShipBatch::getOrderId, shipOrder.getId()).remove()) {
            throw new DaoException("remove ship batch  fail " + shipOrder.getId());
        }

        BigDecimal weight = shipOrder.getWeight();
        BigDecimal[] divideAndReminder = weight.divideAndRemainder(BigDecimal.valueOf(batchSize));
        List<ShipBatch> shipBatches =
            IntStream.range(1, batchSize + 1).mapToObj(i -> new ShipBatch().setOrderId(shipOrder.getId()).setWeight(divideAndReminder[0]).setSort(i)).collect(Collectors.toList());
        shipBatches.get(batchSize - 1).setWeight(divideAndReminder[0].add(divideAndReminder[1]));
        shipBatchService.saveBatch(shipBatches);

        shipOrder.setBatchSize(batchSize);
        if (!updateOrder(shipOrder)) {
            throw new DaoException("split when updateOrder fail");
        }
        return shipBatches;
    }

    @Override
    public ShipOrder merge(Long id) throws Exception {
        ShipOrder shipOrder =
            Optional.ofNullable(lambdaQuery().eq(ShipOrder::getId, id).one()).orElseThrow(() -> new DaoException("item not " +
                "found"));
        if (!shipBatchService.lambdaUpdate().eq(ShipBatch::getOrderId, id).remove()) {
            throw new DaoException("remove ship batch  fail " + id);
        }

        shipBatchService.save(new ShipBatch().setOrderId(id).setSort(1).setWeight(shipOrder.getWeight()));

        shipOrder.setBatchSize(1);
        if (!updateOrder(shipOrder)) {
            throw new DaoException("split when updateOrder fail");
        }
        return shipOrder;
    }

    private void mergeInternal(ShipOrder shipOrder) throws Exception {
        if (!shipBatchService.lambdaUpdate().eq(ShipBatch::getOrderId, shipOrder.getId()).remove()) {
            throw new DaoException("remove ship batch  fail " + shipOrder.getId());
        }

        shipBatchService.save(new ShipBatch().setOrderId(shipOrder.getId()).setSort(1).setWeight(shipOrder.getWeight()));

        shipOrder.setBatchSize(1);
        if (!updateOrder(shipOrder)) {
            throw new DaoException("split when updateOrder fail");
        }
    }

    @Override
    public ShipOrder changeRootQuantity(ShipOrder shipOrder) throws Exception {
        if (!updateOrder(shipOrder)) {
            throw new DaoException("changeRootQuantity fail");
        }

        Integer batchSize = shipOrder.getBatchSize();
        if (batchSize <= 0) {
            throw new DaoException(" ship patch size should be greater then 0");
        } else if (batchSize == 1) {
            mergeInternal(shipOrder);
        } else {
            splitInternal(shipOrder, batchSize);
        }
        return shipOrder;
    }

}
