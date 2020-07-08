package com.cienet.shipment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cienet.shipment.domain.ShipBatch;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.exception.DaoException;
import com.cienet.shipment.exception.ShipmentException;
import com.cienet.shipment.repository.ShipOrderRepo;
import com.cienet.shipment.service.OrderService;
import com.cienet.shipment.service.ShipBatchService;
import com.cienet.shipment.vo.Paging;
import com.cienet.shipment.vo.ShipOrderQueryVo;
import com.cienet.shipment.vo.param.ShipOrderParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
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

    private static final int SCALE = 3;

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

    @Transactional(readOnly = true)
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ShipBatch> split(Long id, Double[] quantities) throws Exception {
        ShipOrder shipOrder =
            Optional.ofNullable(lambdaQuery().eq(ShipOrder::getId, id).one()).orElseThrow(() -> new DaoException("item not " +
                "found"));

        return splitInternal(shipOrder, quantities);
    }

    private BigDecimal bdScale(BigDecimal decimal) {
        return decimal.setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private boolean checkOrderQuantitySum(BigDecimal total, Double[] shipBatches) {
        BigDecimal sum = BigDecimal.ZERO.setScale(SCALE);
        for (Double shipBatch : shipBatches) {
            sum = sum.add(bdScale(BigDecimal.valueOf(shipBatch)));
        }
        return total.compareTo(sum) == 0;
    }

    private List<ShipBatch> splitInternal(ShipOrder shipOrder, Double[] quantities) throws Exception {
        BigDecimal weight = shipOrder.getWeight();
        log.debug("weight = {}, quantities {}", weight, quantities);
        if (!checkOrderQuantitySum(weight, quantities)) {
            throw new ShipmentException("order quantity is not equal batches");
        }

        if (!shipBatchService.lambdaUpdate().eq(ShipBatch::getOrderId, shipOrder.getId()).remove()) {
            throw new DaoException("remove ship batch  fail " + shipOrder.getId());
        }

        List<ShipBatch> shipBatches =
            IntStream.range(0, quantities.length)
                .mapToObj(i -> new ShipBatch()
                    .setOrderId(shipOrder.getId())
                    .setWeight(bdScale(BigDecimal.valueOf(quantities[i])))
                    .setSort(i))
                .collect(Collectors.toList());
        shipBatchService.saveBatch(shipBatches);

        shipOrder.setBatchSize(quantities.length);
        if (!updateOrder(shipOrder)) {
            throw new DaoException("split when updateOrder fail");
        }
        return shipBatches;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ShipBatch> merge(Long id, Long[] batchIds) throws Exception {
        ShipOrder shipOrder =
            Optional.ofNullable(lambdaQuery().eq(ShipOrder::getId, id).one()).orElseThrow(() -> new DaoException("item not " +
                "found"));
        mergeInternal(shipOrder, batchIds);
        return shipBatchService.lambdaQuery().eq(ShipBatch::getOrderId, id).list();
    }

    private void mergeInternal(ShipOrder shipOrder, Long[] batchIds) throws Exception {
        List<ShipBatch> mergeBatches = shipBatchService.lambdaQuery().in(ShipBatch::getId, batchIds).list();
        if (mergeBatches.size() == 0) {
            log.warn("there are no batches for order {}", shipOrder);
            return;
        }
        if (!shipBatchService.removeByIds(Arrays.asList(batchIds))) {
            throw new DaoException("remove ship batch  fail " + shipOrder.getId());
        }
        ShipBatch mergeBatch =
            mergeBatches.stream().reduce((a, b) -> new ShipBatch().setWeight(a.getWeight().add(b.getWeight()))).get();
        mergeBatch.setOrderId(shipOrder.getId());
        shipBatchService.save(mergeBatch);

        shipOrder.setBatchSize(shipOrder.getBatchSize() - batchIds.length + 1);
        if (!updateOrder(shipOrder)) {
            throw new DaoException("mergeInternal when updateOrder fail");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ShipBatch> changeOrderQuantity(Long id, Double q) throws Exception {
        if (q <= 0) {
            throw new DaoException(" ship patch size should be greater then 0");
        }
        // query the ship order
        ShipOrder shipOrder =
            Optional.ofNullable(lambdaQuery().eq(ShipOrder::getId, id).one()).orElseThrow(() -> new DaoException("item not " +
                "found"));
        BigDecimal newQuantity = bdScale(BigDecimal.valueOf(q));
        BigDecimal oldQuantity = shipOrder.getWeight();

        // update batches
        List<ShipBatch> shipBatches = shipBatchService.lambdaQuery().eq(ShipBatch::getOrderId, id).list();
        BigDecimal ratio = newQuantity.divide(oldQuantity, SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal sum = bdScale(BigDecimal.ZERO);
        log.debug("old = {}, new = {}, sum = {}, ratio = {}", oldQuantity, newQuantity, sum, ratio);
        for (ShipBatch shipBatch : shipBatches) {
            BigDecimal w = bdScale(shipBatch.getWeight().multiply(ratio));
            shipBatch.setWeight(w);
            sum = sum.add(w);
        }
        BigDecimal left = newQuantity.subtract(sum);
        log.debug("left = {}", left);
        ShipBatch sb = shipBatches.get(0);
        sb.setWeight(sb.getWeight().add(left));
        shipBatchService.updateBatchById(shipBatches);

        shipOrder.setWeight(newQuantity);
        if (!updateOrder(shipOrder)) {
            throw new DaoException("update order fail");
        }
        return shipBatches;
    }

}
