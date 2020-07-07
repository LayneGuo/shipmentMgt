package com.cienet.shipment.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.web.rest.param.ShipOrderParam;
import com.cienet.shipment.web.rest.vm.ShipOrderQueryVo;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

public interface ShipOrderRepo extends BaseMapper<ShipOrder> {
    /**
     *
     * @param id
     * @return
     */
    ShipOrderQueryVo getIpById(Serializable id);

    /**
     *
     * @param page
     * @param ipQueryParam
     * @return
     */
    IPage<ShipOrderQueryVo> getIpPageList(@Param("page") Page page, @Param("param") ShipOrderParam ipQueryParam);

}
