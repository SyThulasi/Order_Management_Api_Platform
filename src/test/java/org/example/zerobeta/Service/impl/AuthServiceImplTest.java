package org.example.zerobeta.Service.impl;

import org.example.zerobeta.Config.JwtService;
import org.example.zerobeta.DTO.AuthenticationRequestDto;
import org.example.zerobeta.DTO.AuthenticationResponseDTO;
import org.example.zerobeta.DTO.RegisterRequestDto;
import org.example.zerobeta.Model.Client;
import org.example.zerobeta.Repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("John", "Doe", "john.doe@example.com", "password");
        Client client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");
        client.setPassword("encodedPassword");

        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(jwtService.generateToken(any(Client.class))).thenReturn("jwtToken");

        // Act
        ResponseEntity<AuthenticationResponseDTO> response = authService.register(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Client registered successfully!", response.getBody().getMessage());
        assertEquals("jwtToken", response.getBody().getToken());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("Jane", "Doe", "jane.doe@example.com", "password");
        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new Client()));

        // Act
        ResponseEntity<AuthenticationResponseDTO> response = authService.register(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already exists: jane.doe@example.com", response.getBody().getMessage());
    }

    @Test
    void testRegister_DataIntegrityViolation() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("Jane", "Doe", "jane.doe@example.com", "password");
        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        // Act
        ResponseEntity<AuthenticationResponseDTO> response = authService.register(request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("An error occurred: Duplicate entry detected.", response.getBody().getMessage());
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto("john.doe@example.com", "password");
        Client client = new Client();
        client.setId(1L);
        client.setEmail("john.doe@example.com");
        client.setPassword("encodedPassword");

        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(client));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(client)).thenReturn("jwtToken");

        // Mock successful authentication
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Act
        ResponseEntity<AuthenticationResponseDTO> response = authService.authenticate(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Client logged in successfully!", response.getBody().getMessage());
        assertEquals("jwtToken", response.getBody().getToken());
    }


    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto("unknown@example.com", "password");
        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<AuthenticationResponseDTO> response = authService.authenticate(request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with email: unknown@example.com", response.getBody().getMessage());
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto("john.doe@example.com", "wrongPassword");
        Client client = new Client();
        client.setId(1L);
        client.setEmail("john.doe@example.com");

        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(client));
        doThrow(new BadCredentialsException("Invalid credentials")).when(authenticationManager).authenticate(any());

        // Act
        ResponseEntity<AuthenticationResponseDTO> response = authService.authenticate(request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials: Incorrect password.", response.getBody().getMessage());
    }
}
