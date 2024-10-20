package org.example.zerobeta.service;

import org.example.zerobeta.dto.OrderRequestDTO;
import org.example.zerobeta.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO);
    String cancelOrder(Long orderId);
    List<OrderResponseDTO> getOrderHistory(int page, int size);
    void updateNewOrdersToDispatched();
}
