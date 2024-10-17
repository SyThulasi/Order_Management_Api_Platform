package org.example.zerobeta.Service;

import org.example.zerobeta.DTO.AuthenticationRequestDto;
import org.example.zerobeta.DTO.RegisterRequestDto;
import org.example.zerobeta.DTO.AuthenticationResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<AuthenticationResponseDTO> register(RegisterRequestDto request);
    ResponseEntity<AuthenticationResponseDTO> authenticate(AuthenticationRequestDto request);
}