package org.example.zerobeta.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    // Item name must be at least 2 characters long and cannot be blank
    @NotBlank(message = "Item name is required")
    @Size(min = 2, message = "Item name must be at least 2 characters long")
    private String itemName;

    // Quantity must be at least 1 and cannot be null
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Shipping address cannot be blank
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
}
