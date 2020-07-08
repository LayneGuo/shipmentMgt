package com.cienet.shipment.web.rest;

import com.cienet.shipment.ShipmentMgtApp;
import com.cienet.shipment.domain.ShipBatch;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.exception.GlobalExceptionHandler;
import com.cienet.shipment.service.OrderService;
import com.cienet.shipment.service.ShipBatchService;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link ShipmentResource} REST controller.
 */
@SpringBootTest(classes = ShipmentMgtApp.class)
public class ShipmentResourceIT {


    @Autowired
    private OrderService orderService;
    @Autowired
    private ShipBatchService shipBatchService;
    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private GlobalExceptionHandler exceptionTranslator;

    private MockMvc shipmentMockMvc;


    @BeforeEach
    public void setup() {
        ShipmentResource shipmentResource = new ShipmentResource(orderService);

        this.shipmentMockMvc = MockMvcBuilders.standaloneSetup(shipmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter)
            .build();
    }

    @Test
    @Transactional
    public void testSplitWhenBatchSizeIsNegative() throws Exception {

        shipmentMockMvc.perform(get("/api/ship/split/{id}", 1).queryParam("q", "-1")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testSplitWhenWrongQuantityThenCode500() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/split/{id}", 1).queryParam("q", "5")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testSplitWhenBatchSize() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/split/{id}", 1).queryParam("q", "20.500")
            .queryParam("q", "29.500")
            .queryParam("q", "50.000")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());

        // Validate the ship order and ship batch in the database
        ShipOrder shipOrder = orderService.getById(1);
        List<ShipBatch> shipBatches = shipBatchService.lambdaQuery().eq(ShipBatch::getOrderId, 1).list();
        assertThat(shipBatches).hasSize(3);
        BigDecimal sum = BigDecimal.ZERO;
        for (ShipBatch shipBatch : shipBatches) {
            sum = sum.add(shipBatch.getWeight());
        }
        assertThat(sum).isCloseTo(shipOrder.getWeight(), Percentage.withPercentage(0));
    }

    @Test
    @Transactional
    public void testSplitWhenOrderIdNotExist() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/split/{id}", 10000).queryParam("q", "5")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.msg").value("item not found"))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testMerge() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/merge/{id}", 3).queryParam("bId", "3").queryParam("bId", "4")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());

        // Validate the ship order and ship batch in the database
        ShipOrder shipOrder = orderService.getById(3);
        List<ShipBatch> shipBatches = shipBatchService.lambdaQuery().eq(ShipBatch::getOrderId, 3).list();
        assertThat(shipBatches).hasSize(2);
        BigDecimal sum = BigDecimal.ZERO;
        for (ShipBatch shipBatch : shipBatches) {
            sum = sum.add(shipBatch.getWeight());
        }
        assertThat(sum).isCloseTo(shipOrder.getWeight(), Percentage.withPercentage(0));
    }

    @Test
    @Transactional
    public void tesChangeOrderQuantity() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/change-quantity/{id}", 3).queryParam("q", "21.111")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());

        // Validate the ship order and ship batch in the database
        ShipOrder shipOrder = orderService.getById(3);
        List<ShipBatch> shipBatches = shipBatchService.lambdaQuery().eq(ShipBatch::getOrderId, 3).list();
        assertThat(shipBatches).hasSize(3);
        BigDecimal sum = BigDecimal.ZERO;
        for (ShipBatch shipBatch : shipBatches) {
            sum = sum.add(shipBatch.getWeight());
        }
        assertThat(sum).isCloseTo(shipOrder.getWeight(), Percentage.withPercentage(0));
    }
}
