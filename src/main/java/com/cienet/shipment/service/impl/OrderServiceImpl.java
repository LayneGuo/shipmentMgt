package com.cienet.shipment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.exception.DaoException;
import com.cienet.shipment.exception.ShipmentException;
import com.cienet.shipment.repository.ShipOrderRepo;
import com.cienet.shipment.service.OrderService;
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
        Page page = setPageParam(orderQueryParam, OrderItem.desc("trade_no"));
        IPage<ShipOrderQueryVo> iPage = orderMapper.getShipOrderPageList(page, orderQueryParam);
        return new Paging(iPage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ShipOrder> split(String tradeNo, Long id, Double[] quantities) throws Exception {
        ShipOrder shipOrder = Optional.ofNullable(lambdaQuery()
            .eq(ShipOrder::getTradeNo, tradeNo)
            .eq(ShipOrder::getId, id).one())
            .orElseThrow(() -> new DaoException("item not found"));

        return splitInternal(shipOrder, quantities);
    }

    private BigDecimal bdScale(BigDecimal decimal) {
        return decimal.setScale(SCALE, BigDecimal.ROUND_DOWN);
    }

    private boolean checkOrderQuantitySum(BigDecimal total, Double[] shipBatches) {
        BigDecimal sum = BigDecimal.ZERO.setScale(SCALE);
        for (Double shipBatch : shipBatches) {
            sum = sum.add(bdScale(BigDecimal.valueOf(shipBatch)));
        }
        return total.compareTo(sum) == 0;
    }

    private List<ShipOrder> splitInternal(ShipOrder shipOrder, Double[] quantities) throws Exception {
        BigDecimal weight = shipOrder.getWeight();
        log.debug("weight = {}, quantities {}", weight, quantities);
        if (!checkOrderQuantitySum(weight, quantities)) {
            throw new ShipmentException("order quantity is not equal batches");
        }

        List<ShipOrder> shipOrders =
            IntStream.range(0, quantities.length)
                .mapToObj(i -> new ShipOrder()
                    .setTradeNo(shipOrder.getTradeNo())
                    .setWeight(bdScale(BigDecimal.valueOf(quantities[i]))))
                .collect(Collectors.toList());
        saveBatch(shipOrders);

        if (!removeById(shipOrder.getId())) {
            throw new DaoException("remove ship batch  fail " + shipOrder.getId());
        }
        return shipOrders;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShipOrder merge(String tradeNo, Long[] ids) throws Exception {
        List<ShipOrder> shipOrders = findOrdersByTradeNo(tradeNo, ids);
        if (shipOrders.size() <= 0 || ids.length != shipOrders.size()) {
            throw new DaoException("The some Orders are not exist");
        }
        ShipOrder mergeOrder = mergeInternal(shipOrders);
        if (!removeByIds(Arrays.asList(ids))) {
            throw new DaoException("remove ship batch  fail " + ids);
        }
        return mergeOrder;
    }

    private ShipOrder mergeInternal(List<ShipOrder> shipOrders) throws Exception {
        ShipOrder mergeOrder =
            shipOrders.stream()
                .reduce((a, b) -> new ShipOrder().setWeight(a.getWeight().add(b.getWeight())).setTradeNo(a.getTradeNo())).get();
        if (!save(mergeOrder)) {
            throw new DaoException("mergeInternal when updateOrder fail");
        }
        return mergeOrder;
    }

    private List<ShipOrder> findOrdersByTradeNo(String tradeNo, Long[] ids) {
        return Optional.ofNullable(lambdaQuery().eq(ShipOrder::getTradeNo, tradeNo).in(ShipOrder::getId, ids).list()).orElseThrow(() -> new DaoException("item not " +
            "found"));
    }

    private List<ShipOrder> findOrdersByTradeNo(String tradeNo) {
        return Optional.ofNullable(lambdaQuery().eq(ShipOrder::getTradeNo, tradeNo).list()).orElseThrow(() -> new DaoException("item not " +
            "found"));
    }

    private BigDecimal sumShipOrdersWeight(List<ShipOrder> shipOrders) {
        BigDecimal sum = bdScale(BigDecimal.ZERO);
        for (ShipOrder shipOrder : shipOrders) {
            sum = sum.add(shipOrder.getWeight());
        }
        return sum;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ShipOrder> changeOrderQuantity(String tradeNo, Double q) throws Exception {
        if (q <= 0) {
            throw new DaoException(" ship patch size should be greater then 0");
        }
        // query the ship order
        List<ShipOrder> shipOrders = findOrdersByTradeNo(tradeNo);

        BigDecimal newQuantity = bdScale(BigDecimal.valueOf(q));
        BigDecimal oldQuantity = sumShipOrdersWeight(shipOrders);
        if (oldQuantity.compareTo(newQuantity) == 0) {
            return shipOrders;
        }

        // update batches
        BigDecimal ratio = newQuantity.divide(oldQuantity, SCALE, BigDecimal.ROUND_DOWN);
        BigDecimal sum = bdScale(BigDecimal.ZERO);
        for (ShipOrder shipOrder : shipOrders) {
            BigDecimal w = bdScale(shipOrder.getWeight().multiply(ratio));
            shipOrder.setWeight(w);
            sum = sum.add(w);
        }
        log.debug("old = {}, new = {}, sum = {}, ratio = {}", oldQuantity, newQuantity, sum, ratio);
        BigDecimal left = newQuantity.subtract(sum);
        log.debug("left = {}", left);
        ShipOrder sd = shipOrders.get(0);
        sd.setWeight(sd.getWeight().add(left));
        if (!updateBatchById(shipOrders)) {
            throw new DaoException("update order fail");
        }

        return shipOrders;
    }

}
