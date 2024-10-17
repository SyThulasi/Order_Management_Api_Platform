package org.example.zerobeta.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.DTO.OrderRequestDTO;
import org.example.zerobeta.DTO.OrderResponseDTO;
import org.example.zerobeta.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place-order")
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.ok(orderService.placeOrder(orderRequestDTO));
    }

}
