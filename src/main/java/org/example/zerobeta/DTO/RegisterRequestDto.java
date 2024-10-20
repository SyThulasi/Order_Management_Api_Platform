package org.example.zerobeta.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "First name is required")  // Ensures first name is not null or empty
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters long") // Sets character limits
    private String firstName;

    @NotBlank(message = "Last name is required")  // Ensures last name is not null or empty
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters long") // Sets character limits
    private String lastName;

    @NotBlank(message = "Email is required")  // Ensures email is not null or empty
    @Email(message = "Invalid email format") // Validates that the email is in the correct format
    private String email;

    @NotBlank(message = "Password is required") // Ensures password is not null or empty
    @Size(min = 8, message = "Password must be at least 8 characters long") // Enforces a minimum length
    private String password;

}
