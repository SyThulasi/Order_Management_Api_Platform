package org.example.zerobeta.Util;

import lombok.RequiredArgsConstructor;
import org.example.zerobeta.Exception.CustomException;
import org.example.zerobeta.Model.Client;
import org.example.zerobeta.Repository.ClientRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final ClientRepository clientRepository;

    public Client getAuthenticatedClient() {

        // Retrieve the authenticated user's details from the SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return clientRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("Client not found"));
        } else {
            throw new CustomException("Authentication failed");
        }
    }
}
