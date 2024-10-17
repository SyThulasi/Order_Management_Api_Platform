package org.example.zerobeta.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.zerobeta.Model.OrderStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private String itemName;
    private int quantity;
    private OrderStatus status;
    private LocalDateTime timestamp;
}

