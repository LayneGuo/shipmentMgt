package com.cienet.shipment.web.rest;

import com.cienet.shipment.ShipmentMgtApp;
import com.cienet.shipment.domain.ShipOrder;
import com.cienet.shipment.exception.GlobalExceptionHandler;
import com.cienet.shipment.service.OrderService;
import com.cienet.shipment.vo.param.ShipOrderParam;
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

import static com.cienet.shipment.web.rest.TestUtil.convertObjectToJsonBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link ShipmentResource} REST controller.
 */
@SpringBootTest(classes = ShipmentMgtApp.class)
public class ShipmentResourceIT {
    private static final String TRADE_NO = "cienet";

    @Autowired
    private OrderService orderService;
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
    public void addShipOrder() throws Exception {
        shipmentMockMvc.perform(post("/api/ship/add")
            .queryParam("q", "-1")
            .content(convertObjectToJsonBytes(new ShipOrder().setWeight(BigDecimal.valueOf(100.000)).setTradeNo("test")))
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void getShipOrderById() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/info/{id}", 1)
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void updateShipOrderById() throws Exception {
        ShipOrder shipOrder = orderService.getById(1);
        shipOrder.setWeight(BigDecimal.valueOf(300.000));
        shipmentMockMvc.perform(post("/api/ship/update")
            .content(convertObjectToJsonBytes(shipOrder))
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void deleteShipOrderById() throws Exception {
        shipmentMockMvc.perform(delete("/api/ship/delete/{id}", 1)
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void pageListOfShipOrders() throws Exception {
        shipmentMockMvc.perform(post("/api/ship/getPageList")
            .content(convertObjectToJsonBytes(new ShipOrderParam()))
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.total").value(7))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testSplitWhenBatchSizeIsNegative() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/split/{tradeNo}/{id}", TRADE_NO, 1)
            .queryParam("q", "-1")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testSplitWhenWrongQuantityThenCode500() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/split/{tradeNo}/{id}", TRADE_NO, 1)
            .queryParam("q", "5")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testSplitWithNewQuantities() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/split/{tradeNo}/{id}", TRADE_NO, 1)
            .queryParam("q", "20.500")
            .queryParam("q", "29.500")
            .queryParam("q", "50.000")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(hasSize(3)))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testSplitWithNewQuantitiesAndOneTrade() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/split/{tradeNo}/{id}", "tic", 7)
            .queryParam("q", "300.000")
            .queryParam("q", "400.000")
            .queryParam("q", "300.000")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(hasSize(3)))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testSplitWhenOrderIdNotExist() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/split/{tradeNo}/{id}", TRADE_NO, 10000)
            .queryParam("q", "5")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.msg").value("item not found"))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testMergeThen200() throws Exception {
        List<ShipOrder> shipOrders = orderService.lambdaQuery().in(ShipOrder::getId, 3, 4).list();
        assertThat(shipOrders).hasSize(2);
        BigDecimal sum = BigDecimal.ZERO;
        for (ShipOrder shipOrder : shipOrders) {
            sum = sum.add(shipOrder.getWeight());
        }
        shipmentMockMvc.perform(get("/api/ship/merge/{tradeNo}", TRADE_NO)
            .queryParam("id", "3")
            .queryParam("id", "4")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.weight").value(sum.doubleValue()))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testMergeWithNotExistIdsThen500() throws Exception {
        shipmentMockMvc.perform(get("/api/ship/merge/{tradeNo}", TRADE_NO)
            .queryParam("id", "2")
            .queryParam("id", "4000")
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void tesChangeOrderQuantity() throws Exception {
        BigDecimal newQuantity = BigDecimal.valueOf(21.111);
        shipmentMockMvc.perform(get("/api/ship/change-quantity/{tradeNo}", TRADE_NO)
            .queryParam("q", newQuantity.toString())
            .contentType(TestUtil.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(status().isOk());

        // Validate the ship order and ship batch in the database
        List<ShipOrder> shipOrders = orderService.lambdaQuery().eq(ShipOrder::getTradeNo, TRADE_NO).list();
        BigDecimal sum = BigDecimal.ZERO;
        for (ShipOrder shipOrder : shipOrders) {
            sum = sum.add(shipOrder.getWeight());
        }
        assertThat(sum).isEqualByComparingTo(newQuantity);
    }
}
