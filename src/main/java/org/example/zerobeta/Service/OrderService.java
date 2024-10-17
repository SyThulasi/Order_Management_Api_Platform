package org.example.zerobeta.Service;

import org.example.zerobeta.DTO.OrderRequestDTO;
import org.example.zerobeta.DTO.OrderResponseDTO;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO);
}
