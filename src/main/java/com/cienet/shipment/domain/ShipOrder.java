package com.cienet.shipment.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "SysDict对象", description = "系统字典表")
public class ShipOrder  extends BaseEntity{
    private BigDecimal weight;
    private Integer batchSize;
}
