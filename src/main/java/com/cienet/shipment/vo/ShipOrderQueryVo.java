package com.cienet.shipment.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipOrderQueryVo {
    private Long id;
    private String tradeNo;
    private BigDecimal weight;
}
