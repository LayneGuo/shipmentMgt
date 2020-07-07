package com.cienet.shipment.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SysDict对象", description = "系统字典表")
public class ShipOrder  extends BaseEntity{
    @ApiModelProperty(value = " 父ID ")
    private Long pid;
}
