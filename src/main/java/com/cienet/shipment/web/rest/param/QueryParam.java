package com.cienet.shipment.web.rest.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("查询参数对象")
public abstract class QueryParam implements Serializable {
    private static final long serialVersionUID = -3263921252635611410L;

    @ApiModelProperty(value = "页码,默认为1", example = "1")
    private Integer current = 1;
    @ApiModelProperty(value = "页大小,默认为10", example = "10")
    private Integer size = 10;
    @ApiModelProperty(value = "搜索字符串", example = "")
    private String keyword;

    public void setCurrent(Integer current) {
        if (current == null || current <= 0) {
            this.current = 1;
        } else {
            this.current = current;
        }
    }

    public void setSize(Integer size) {
        if (size == null || size <= 0) {
            this.size = 10;
        } else {
            this.size = size;
        }
    }

}
