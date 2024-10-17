package org.example.zerobeta.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDto {


    @NotBlank(message = "Email is required") // Ensures email is not null or empty
    @Email(message = "Invalid email format")  // Validates that the email is in the correct format
    private String email;

    @NotBlank(message = "Password is required") // Ensures password is not null or empty
    @Size(min = 8, message = "Password must be at least 8 characters long") // Validates minimum length
    private String password;
}