package org.example.zerobeta.Service.impl;

import org.example.zerobeta.DTO.OrderRequestDTO;
import org.example.zerobeta.DTO.OrderResponseDTO;
import org.example.zerobeta.Exception.CustomException;
import org.example.zerobeta.Model.Client;
import org.example.zerobeta.Model.Order;
import org.example.zerobeta.Model.OrderStatus;
import org.example.zerobeta.Repository.OrderRepository;
import org.example.zerobeta.Util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Client client;
    private OrderRequestDTO orderRequestDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");

        orderRequestDTO = new OrderRequestDTO("item1", 2, "123 Main St");
    }

    @Test
    void testPlaceOrder() {
        // Arrange
        when(securityUtil.getAuthenticatedClient()).thenReturn(client);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // Act
        OrderResponseDTO response = orderService.placeOrder(orderRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("item1", response.getItemName());
        assertEquals(2, response.getQuantity());
        assertEquals(OrderStatus.NEW, response.getStatus());
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);
        order.setClient(client);

        when(securityUtil.getAuthenticatedClient()).thenReturn(client);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        String result = orderService.cancelOrder(1L);

        // Assert
        assertEquals("Order with ID 1 has been canceled successfully.", result);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        // Arrange
        when(securityUtil.getAuthenticatedClient()).thenReturn(client);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> orderService.cancelOrder(1L));
        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void testGetOrderHistory() {
        // Arrange
        Order order1 = new Order();
        order1.setId(1L);
        order1.setItemName("item1");
        order1.setQuantity(2);
        order1.setStatus(OrderStatus.NEW);
        order1.setTimestamp(LocalDateTime.now());
        order1.setClient(client);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setItemName("item2");
        order2.setQuantity(1);
        order2.setStatus(OrderStatus.NEW);
        order2.setTimestamp(LocalDateTime.now());
        order2.setClient(client);

        List<Order> orders = Arrays.asList(order1, order2);
        Page<Order> orderPage = new PageImpl<>(orders);

        when(securityUtil.getAuthenticatedClient()).thenReturn(client);
        when(orderRepository.findByClientId(eq(client.getId()), any(PageRequest.class))).thenReturn(orderPage);

        // Act
        List<OrderResponseDTO> orderHistory = orderService.getOrderHistory(0, 10);

        // Assert
        assertEquals(2, orderHistory.size());
        assertEquals("item1", orderHistory.get(0).getItemName());
        assertEquals("item2", orderHistory.get(1).getItemName());
    }

    @Test
    void testUpdateNewOrdersToDispatched() {
        // Arrange
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.NEW);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(OrderStatus.NEW);

        List<Order> newOrders = Arrays.asList(order1, order2);
        when(orderRepository.findByStatus(OrderStatus.NEW)).thenReturn(newOrders);

        // Act
        orderService.updateNewOrdersToDispatched();

        // Assert
        assertEquals(OrderStatus.DISPATCHED, order1.getStatus());
        assertEquals(OrderStatus.DISPATCHED, order2.getStatus());
        verify(orderRepository, times(2)).save(any(Order.class));
    }
}
