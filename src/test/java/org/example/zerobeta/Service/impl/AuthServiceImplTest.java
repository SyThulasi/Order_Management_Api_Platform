package org.example.zerobeta.Service.impl;

import org.example.zerobeta.Config.JwtService;
import org.example.zerobeta.DTO.AuthenticationRequestDto;
import org.example.zerobeta.DTO.AuthenticationResponseDTO;
import org.example.zerobeta.DTO.RegisterRequestDto;
import org.example.zerobeta.Exception.CustomException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testRegisterSuccess() {
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
    void testRegisterEmailAlreadyExists() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("Jane", "Doe", "jane.doe@example.com", "password");
        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new Client()));

        // Act and Assert
        assertThrows(CustomException.class, () -> {
            authService.register(request);
        });
    }

    @Test
    void testRegisterDataIntegrityViolation() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("Jane", "Doe", "jane.doe@example.com", "password");
        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        // Act and Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            authService.register(request);
        });
    }

    @Test
    void testAuthenticateSuccess() {
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
    void testAuthenticateUserNotFound() {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto("unknown@example.com", "password");
        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomException.class, () -> {
            authService.authenticate(request);
        });
    }

    @Test
    void testAuthenticateInvalidCredentials() {
        // Arrange
        AuthenticationRequestDto request = new AuthenticationRequestDto("john.doe@example.com", "wrongPassword");
        Client client = new Client();
        client.setId(1L);
        client.setEmail("john.doe@example.com");

        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(client));
        doThrow(new BadCredentialsException("Invalid credentials")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act and Assert
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(request);
        });
    }
}
