package com.cienet.shipment.web.rest;

import com.baomidou.mybatisplus.extension.api.ApiController;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.domain.ShipBatch;
import com.cienet.shipment.service.OrderService;
import com.cienet.shipment.vo.ApiResult;
import com.cienet.shipment.vo.Paging;
import com.cienet.shipment.vo.param.ShipOrderParam;
import com.cienet.shipment.vo.ShipOrderQueryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing shipment.
 */
@RestController
@RequestMapping("/api/ship")
public class ShipmentResource extends ApiController {

    private final Logger log = LoggerFactory.getLogger(ShipmentResource.class);

    private final OrderService orderService;

    public ShipmentResource(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/add")
    public ApiResult<Boolean> addPortRelation(@Valid @RequestBody ShipOrder shipOrder) throws Exception {
        boolean flag = orderService.save(shipOrder);
        return ApiResult.result(flag);
    }

    /**
     */
    @PostMapping("/update")
    public ApiResult<Boolean> updatePortRelation(@Valid @RequestBody ShipOrder shipOrder) throws Exception {
        boolean flag = orderService.updateOrder(shipOrder);
        return ApiResult.result(flag);
    }

    /**
     */
    @PostMapping("/delete/{id}")
    public ApiResult<Boolean> deletePortRelation(@PathVariable("id") Long id) throws Exception {
        boolean flag = orderService.deleteOrder(id);
        return ApiResult.result(flag);
    }

    /**
     */
    @GetMapping("/info/{id}")
    public ApiResult<ShipOrderQueryVo> getOrder(@PathVariable("id") Long id) throws Exception {
        ShipOrderQueryVo order = orderService.getOrderById(id);
        return ApiResult.ok(order);
    }

    /**
     */
    @PostMapping("/getPageList")
    public ApiResult<Paging<ShipOrderQueryVo>> getPortRelationPageList(@Valid @RequestBody ShipOrderParam shipOrderParam) throws Exception {
        Paging<ShipOrderQueryVo> paging = orderService.getOrderPageList(shipOrderParam);
        return ApiResult.ok(paging);
    }


    /**
     * {@code GET  /split}
     * split shipment order to given batch size
     */
    @GetMapping("/split/{id}")
    public ApiResult<List<ShipBatch>> split(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer batchSize) throws Exception {
        if (batchSize <= 0) {
            return ApiResult.fail("batch size should not be null");
        }

        return ApiResult.ok(orderService.split(id, batchSize));
    }

    /**
     * {@code GET  /merge}
     * split shipment order to given batch size
     */
    @GetMapping("/merge/{id}")
    public ApiResult<ShipOrder> merge(@PathVariable Long id) throws Exception {
        return ApiResult.ok(orderService.merge(id));
    }

    /**
     * {@code POST  /change-root-quantity}
     * change quantity for the given order
     */
    @PostMapping("/change-quantity")
    public ApiResult<ShipOrder> merge(@RequestBody ShipOrder shipOrder) throws Exception {
        return ApiResult.ok(orderService.changeRootQuantity(shipOrder));
    }
}
