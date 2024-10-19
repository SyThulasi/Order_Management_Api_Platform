package org.example.zerobeta.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.DTO.OrderRequestDTO;
import org.example.zerobeta.DTO.OrderResponseDTO;
import org.example.zerobeta.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Endpoint for placing a new order
    @PostMapping("/place-order")
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.ok(orderService.placeOrder(orderRequestDTO));
    }

    // Endpoint for canceling an existing order
    @PutMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(@RequestParam Long orderId) {
        String responseMessage = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(responseMessage);
    }

    // Endpoint for fetching order history with pagination
    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<OrderResponseDTO> orderHistory = orderService.getOrderHistory(page, size);
        return ResponseEntity.ok(orderHistory);
    }

}
