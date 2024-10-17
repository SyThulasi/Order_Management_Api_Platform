package org.example.zerobeta.Service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.DTO.OrderRequestDTO;
import org.example.zerobeta.DTO.OrderResponseDTO;
import org.example.zerobeta.Model.Client;
import org.example.zerobeta.Model.Order;
import org.example.zerobeta.Model.OrderStatus;
import org.example.zerobeta.Repository.OrderRepository;
import org.example.zerobeta.Service.OrderService;
import org.example.zerobeta.Util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    @Override
    public OrderResponseDTO placeOrder(@Valid OrderRequestDTO orderRequestDTO) {

        // Get the authenticated client
        Client client = securityUtil.getAuthenticatedClient();

        // Create the order object and set its initial status
        Order order = Order.builder()
                .itemName(orderRequestDTO.getItemName())
                .quantity(orderRequestDTO.getQuantity())
                .shippingAddress(orderRequestDTO.getShippingAddress())
                .status(OrderStatus.NEW)
                .client(client)
                .build();

        // Save the order in the database
        Order savedOrder = orderRepository.save(order);

        // Return the response DTO
        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getItemName(),
                savedOrder.getQuantity(),
                savedOrder.getStatus(),
                savedOrder.getTimestamp()
        );
    }
}
