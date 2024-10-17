package org.example.zerobeta.Service;

import org.example.zerobeta.DTO.OrderRequestDTO;
import org.example.zerobeta.DTO.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO);
    String cancelOrder(Long orderId);
    List<OrderResponseDTO> getOrderHistory(int page, int size);
    void updateNewOrdersToDispatched();
}
