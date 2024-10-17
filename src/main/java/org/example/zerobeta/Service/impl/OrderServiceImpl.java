package org.example.zerobeta.Service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.DTO.OrderRequestDTO;
import org.example.zerobeta.DTO.OrderResponseDTO;
import org.example.zerobeta.Exception.CustomException;
import org.example.zerobeta.Model.Client;
import org.example.zerobeta.Model.Order;
import org.example.zerobeta.Model.OrderStatus;
import org.example.zerobeta.Repository.OrderRepository;
import org.example.zerobeta.Service.OrderService;
import org.example.zerobeta.Util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public String cancelOrder(Long orderId) {
        // Find the order by ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        // Check if the order is in NEW status
        if (!order.getStatus().equals(OrderStatus.NEW)) {
            throw new CustomException("Order cannot be canceled as it is not in NEW status");
        }

        // Update the order status to CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order); // Save the updated order

        return "Order with ID " + orderId + " has been canceled successfully.";
    }

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

    // Fetch all NEW orders and update them to DISPATCHED
    @Transactional
    public void updateNewOrdersToDispatched() {
        // Find all NEW orders
        List<Order> newOrders = orderRepository.findByStatus(OrderStatus.NEW);

        // Update each order's status to DISPATCHED
        newOrders.forEach(order -> {
            order.setStatus(OrderStatus.DISPATCHED);
            orderRepository.save(order);  // Save the updated order
        });

        System.out.println("Updated " + newOrders.size() + " orders from NEW to DISPATCHED.");
    }
}
