package com.cienet.shipment.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cienet.shipment.domain.ShipBatch;
import com.cienet.shipment.vo.param.ShipBatchParam;
import com.cienet.shipment.vo.ShipBatchQueryVo;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

public interface ShipBatchRepo extends BaseMapper<ShipBatch> {
    /**
     * @param id
     * @return
     */
    ShipBatchQueryVo getShipBatchById(Serializable id);

    /**
     * @param page
     * @param shipBatchParam
     * @return
     */
    IPage<ShipBatchQueryVo> getShipBatchPageList(@Param("page") Page page,
                                                 @Param("param") ShipBatchParam shipBatchParam);

}
