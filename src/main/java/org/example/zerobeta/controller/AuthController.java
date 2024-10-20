package org.example.zerobeta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.dto.AuthenticationRequestDto;
import org.example.zerobeta.dto.RegisterRequestDto;
import org.example.zerobeta.dto.AuthenticationResponseDTO;
import org.example.zerobeta.service.AuthService;
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

    // Handle Client registration
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        return authService.register(request);
    }

    // Handle user authentication (login)
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequestDto request
    ) {
        return authService.authenticate(request);
    }
}
