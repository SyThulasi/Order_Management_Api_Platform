package org.example.zerobeta.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.DTO.CancelOrderRequestDTO;
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

    @PostMapping("/place-order")
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.ok(orderService.placeOrder(orderRequestDTO));
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(@Valid @RequestBody CancelOrderRequestDTO cancelOrderRequest) {
        String responseMessage = orderService.cancelOrder(cancelOrderRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(
            @RequestParam Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Fetch the order history using the service method
        List<OrderResponseDTO> orderHistory = orderService.getOrderHistory(clientId, page, size);

        // Return the response entity with the list of paginated orders
        return ResponseEntity.ok(orderHistory);
    }

}
