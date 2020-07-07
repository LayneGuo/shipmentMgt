package com.cienet.shipment.web.rest.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "IpQueryParam对象", description = "IP地址查询参数")
public class ShipOrderParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
