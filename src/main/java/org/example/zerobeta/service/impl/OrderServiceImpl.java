package org.example.zerobeta.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.dto.OrderRequestDTO;
import org.example.zerobeta.dto.OrderResponseDTO;
import org.example.zerobeta.exception.CustomException;
import org.example.zerobeta.model.Client;
import org.example.zerobeta.model.Order;
import org.example.zerobeta.model.OrderStatus;
import org.example.zerobeta.repository.OrderRepository;
import org.example.zerobeta.service.OrderService;
import org.example.zerobeta.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final SecurityUtil securityUtil;

    // Place a new order
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

    // Cancel an existing order
    @Transactional
    public String cancelOrder(Long orderId) {

        // Get the authenticated client
        Client client = securityUtil.getAuthenticatedClient();

        // Find the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        if (!client.getId().equals(order.getClient().getId())){
            throw new CustomException("You are not authorized to cancel this order.");
        }

        if (order.getStatus().equals(OrderStatus.CANCELLED)){
            throw new CustomException("This order is already cancelled.");
        }

        // Check if the order is in NEW status
        if (!order.getStatus().equals(OrderStatus.NEW)) {
            throw new CustomException("Order cannot be canceled as it is not in NEW status");
        }

        // Update the order status to CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order); // Save the updated order

        return "Order with ID " + orderId + " has been canceled successfully.";
    }

    // Fetch paginated order history for a client
    @Override
    public List<OrderResponseDTO> getOrderHistory(int page, int size) {

        // Get the authenticated client
        Client client = securityUtil.getAuthenticatedClient();

        // Create a PageRequest object
        PageRequest pageRequest = PageRequest.of(page, size);

        // Fetch the paginated orders for the client
        Page<Order> orderPage = orderRepository.findByClientId(client.getId(), pageRequest);

        // Convert the orders to OrderResponseDTO
        return orderPage.stream()
                .map(order -> new OrderResponseDTO(
                        order.getId(),
                        order.getItemName(),
                        order.getQuantity(),
                        order.getStatus(),
                        order.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    // Update all NEW orders to DISPATCH
    @Transactional
    public void updateNewOrdersToDispatched() {
        // Find all NEW orders
        List<Order> newOrders = orderRepository.findByStatus(OrderStatus.NEW);

        // Update each order's status to DISPATCH
        newOrders.forEach(order -> {
            order.setStatus(OrderStatus.DISPATCHED);
            orderRepository.save(order);  // Save the updated order
        });

        log.info("Updated {} orders from NEW to DISPATCHED.", newOrders.size());
    }
}
