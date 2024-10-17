package org.example.zerobeta.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.DTO.AuthenticationRequestDto;
import org.example.zerobeta.DTO.RegisterRequestDto;
import org.example.zerobeta.DTO.AuthenticationResponseDTO;
import org.example.zerobeta.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        return authService.register(request);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequestDto request
    ) {
        return authService.authenticate(request);
    }
}
