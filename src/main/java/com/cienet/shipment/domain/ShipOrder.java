package com.cienet.shipment.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel(value = "shipment order")
@Data
@Accessors(chain = true)
public class ShipOrder extends BaseEntity {
    private String description;
    private String tag;
    @NotNull(message = "weight not be null")
    private BigDecimal weight;
    private Integer batchSize;
}
