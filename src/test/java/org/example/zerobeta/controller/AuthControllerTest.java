package org.example.zerobeta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.zerobeta.config.JwtService;
import org.example.zerobeta.dto.AuthenticationRequestDto;
import org.example.zerobeta.dto.AuthenticationResponseDTO;
import org.example.zerobeta.dto.RegisterRequestDto;
import org.example.zerobeta.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto("John", "Doe", "john@example.com", "password");
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO("dummyToken", "Client registered successfully!");
        when(authService.register(registerRequest)).thenReturn(ResponseEntity.ok(responseDTO));
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummyToken"))
                .andExpect(jsonPath("$.message").value("Client registered successfully!"));
    }

    @Test
    void testRegisterInvalidRequest() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto("", "", "invalid_email", "");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        AuthenticationRequestDto authRequest = new AuthenticationRequestDto("john@example.com", "password");
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO("dummyToken", "Client logged in successfully!");
        when(authService.authenticate(authRequest)).thenReturn(ResponseEntity.ok(responseDTO));
        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummyToken"))
                .andExpect(jsonPath("$.message").value("Client logged in successfully!"));
    }

    @Test
    void testAuthenticateInvalidCredentials() throws Exception {
        AuthenticationRequestDto authRequest = new AuthenticationRequestDto("john@example.com", "wrong_password");
        when(authService.authenticate(authRequest)).thenReturn(ResponseEntity.status(401).body(new AuthenticationResponseDTO(null, "Invalid credentials")));
        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

}
