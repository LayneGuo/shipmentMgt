package com.cienet.shipment.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@ApiModel(value = "shipment batch")
@Data
@Accessors(chain = true)
public class ShipBatch extends BaseEntity {

    private Long orderId;
    private BigDecimal weight;
    private Integer sort;
}
