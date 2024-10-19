package org.example.zerobeta.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.zerobeta.Config.JwtService;
import org.example.zerobeta.DTO.OrderRequestDTO;
import org.example.zerobeta.DTO.OrderResponseDTO;
import org.example.zerobeta.Exception.CustomException;
import org.example.zerobeta.Model.OrderStatus;
import org.example.zerobeta.Service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = "testUser@gmail.com", roles = {"USER"})
    void testPlaceOrderSuccess() throws Exception {

        OrderRequestDTO orderRequest = new OrderRequestDTO("Item Name", 2, "123 Address");
        OrderResponseDTO orderResponse = new OrderResponseDTO(1L, "Item Name", 2, OrderStatus.NEW, null);

        when(orderService.placeOrder(any(OrderRequestDTO.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/api/v1/order/place-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Item Name"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    public void testPlaceOrderInvalidRequest() throws Exception {
        String invalidOrderRequestJson = "{ \"itemName\": \"\", \"quantity\": 2, \"shippingAddress\": \"123 Address\" }";

        mockMvc.perform(post("/api/v1/order/place-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOrderRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCancelOrderSuccess() throws Exception {
        Long orderId = 1L;
        String expectedResponse = "Order with ID " + orderId + " has been canceled successfully.";

        when(orderService.cancelOrder(orderId)).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/order/cancel-order")
                        .param("orderId", String.valueOf(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedResponse));
    }

    @Test
    void testCancelOrderFail() throws Exception {
        Long orderId = 1L;
        String expectedErrorMessage = "Order cannot be canceled as it is not in NEW status";

        when(orderService.cancelOrder(orderId)).thenThrow(new CustomException(expectedErrorMessage));

        mockMvc.perform(put("/api/v1/order/cancel-order")
                        .param("orderId", String.valueOf(orderId)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResolvedException().getMessage()));
    }

    @Test
    void testGetOrderHistorySuccess() throws Exception {
        List<OrderResponseDTO> orderHistory = Arrays.asList(
                new OrderResponseDTO(1L, "Item 1", 1, OrderStatus.NEW, null),
                new OrderResponseDTO(2L, "Item 2", 2, OrderStatus.NEW, null)
        );
        when(orderService.getOrderHistory(0, 10)).thenReturn(orderHistory);

        mockMvc.perform(get("/api/v1/order/history")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemName").value("Item 1"))
                .andExpect(jsonPath("$[1].itemName").value("Item 2"));
    }

    @Test
    void testGetOrderHistoryFailClientNotFound() throws Exception {
        int page = 0;
        int size = 10;
        String expectedErrorMessage = "Client not found";

        when(orderService.getOrderHistory(page, size)).thenThrow(new CustomException(expectedErrorMessage));

        mockMvc.perform(get("/api/v1/order/history")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(expectedErrorMessage, result.getResolvedException().getMessage()));
    }
}
