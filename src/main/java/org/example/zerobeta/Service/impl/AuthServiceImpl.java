package org.example.zerobeta.Service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.zerobeta.Config.JwtService;
import org.example.zerobeta.DTO.AuthenticationRequestDto;
import org.example.zerobeta.DTO.RegisterRequestDto;
import org.example.zerobeta.Model.Client;
import org.example.zerobeta.Repository.ClientRepository;
import org.example.zerobeta.DTO.AuthenticationResponseDTO;
import org.example.zerobeta.Service.AuthService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
            return createResponseEntity(null, "Email already exists: " + request.getEmail(), HttpStatus.BAD_REQUEST);
        }

        // Create new client
        var client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        try {
            clientRepository.save(client);
            var jwtToken = jwtService.generateToken(client);
            return createResponseEntity(jwtToken, "Client registered successfully!", HttpStatus.CREATED);

        } catch (DataIntegrityViolationException e) {
            return createResponseEntity(null, "An error occurred: Duplicate entry detected.", HttpStatus.CONFLICT);

        } catch (Exception e) {
            return createResponseEntity(null, "An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    // Handles user authentication (login)
    @Override
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@Valid AuthenticationRequestDto request) {

        var clientOptional = clientRepository.findByEmail(request.getEmail());
        if (clientOptional.isEmpty()) {
            return createResponseEntity(null, "User not found with email: " + request.getEmail(), HttpStatus.NOT_FOUND);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var client = clientOptional.get();
            var jwtToken = jwtService.generateToken(client);
            return createResponseEntity(jwtToken,"Client logged in successfully!",HttpStatus.OK);

        } catch (BadCredentialsException e) {
            return createResponseEntity(null, "Invalid credentials: Incorrect password.", HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            return createResponseEntity(null, "An error occurred during authentication: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Utility method to create response entity for authentication/register requests
    private ResponseEntity<AuthenticationResponseDTO> createResponseEntity(String token, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(AuthenticationResponseDTO.builder()
                .token(token)
                .message(message)
                .build());
    }
}
