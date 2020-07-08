package com.cienet.shipment.vo.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ShipBatchParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
