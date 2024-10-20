package org.example.zerobeta.service;

import org.example.zerobeta.dto.AuthenticationRequestDto;
import org.example.zerobeta.dto.RegisterRequestDto;
import org.example.zerobeta.dto.AuthenticationResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<AuthenticationResponseDTO> register(RegisterRequestDto request);
    ResponseEntity<AuthenticationResponseDTO> authenticate(AuthenticationRequestDto request);
}