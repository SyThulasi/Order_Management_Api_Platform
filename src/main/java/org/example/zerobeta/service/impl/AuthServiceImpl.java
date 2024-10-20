package org.example.zerobeta.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.config.JwtService;
import org.example.zerobeta.dto.AuthenticationRequestDto;
import org.example.zerobeta.dto.RegisterRequestDto;
import org.example.zerobeta.exception.CustomException;
import org.example.zerobeta.model.Client;
import org.example.zerobeta.repository.ClientRepository;
import org.example.zerobeta.dto.AuthenticationResponseDTO;
import org.example.zerobeta.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Handles user registration
    @Override
    public ResponseEntity<AuthenticationResponseDTO> register(@Valid RegisterRequestDto request) {

        // Check if email is already registered
        if (clientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already exists: " + request.getEmail());
        }

        // Create new client
        var client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        clientRepository.save(client);
        var jwtToken = jwtService.generateToken(client);

        return createResponseEntity(jwtToken, "Client registered successfully!", HttpStatus.CREATED);
    }

    // Handles user authentication (login)
    @Override
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@Valid AuthenticationRequestDto request) {

        var clientOptional = clientRepository.findByEmail(request.getEmail());

        if (clientOptional.isEmpty()) {
            throw new CustomException("User not found with email: " + request.getEmail());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var client = clientOptional.get();
        var jwtToken = jwtService.generateToken(client);

        return createResponseEntity(jwtToken, "Client logged in successfully!", HttpStatus.OK);
    }

    // Utility method to create response entity for authentication/register requests
    private ResponseEntity<AuthenticationResponseDTO> createResponseEntity(
            String token,
            String message,
            HttpStatus status
    ) {
        return ResponseEntity.status(status).body(AuthenticationResponseDTO.builder()
                .token(token)
                .message(message)
                .build());
    }
}
