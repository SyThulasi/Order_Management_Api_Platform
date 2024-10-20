package org.example.zerobeta.util;

import lombok.RequiredArgsConstructor;
import org.example.zerobeta.exception.CustomException;
import org.example.zerobeta.model.Client;
import org.example.zerobeta.repository.ClientRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class to retrieve the authenticated client from the security context.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final ClientRepository clientRepository;

    public Client getAuthenticatedClient() {

        // Retrieve the authenticated user's details from the SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            // Find and return the client by email
            return clientRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("Client not found"));
        } else {
            throw new CustomException("Authentication failed");
        }
    }
}
