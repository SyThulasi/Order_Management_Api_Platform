package org.example.zerobeta.util;

import org.example.zerobeta.exception.CustomException;
import org.example.zerobeta.model.Client;
import org.example.zerobeta.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private SecurityUtil securityUtil;

    private Client client;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(client.getEmail());
    }

    @Test
    void testGetAuthenticatedClient_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(clientRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));

        // Act
        Client authenticatedClient = securityUtil.getAuthenticatedClient();

        // Assert
        assertNotNull(authenticatedClient);
        assertEquals(client.getId(), authenticatedClient.getId());
        assertEquals(client.getEmail(), authenticatedClient.getEmail());
    }

    @Test
    void testGetAuthenticatedClient_ClientNotFound() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(clientRepository.findByEmail(client.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> securityUtil.getAuthenticatedClient());
        assertEquals("Client not found", exception.getMessage());
    }

}
