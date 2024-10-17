package org.example.zerobeta.Config;

import lombok.RequiredArgsConstructor;
import org.example.zerobeta.Repository.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ApplicationConfig class provides the configuration for Spring Security components.
 * It defines beans for user authentication, password encoding, and user details retrieval.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final ClientRepository clientRepository;

    /**
     * Bean for retrieving user details based on the provided username (email).
     * This service loads the user by their email and throws an exception if the user is not found.
     *
     * @return UserDetailsService that loads user details based on email.
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> clientRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Bean for setting up the AuthenticationProvider, which is responsible for authenticating users.
     * It uses the DaoAuthenticationProvider to authenticate users against the UserDetailsService and
     * checks the encoded password with the BCryptPasswordEncoder.
     *
     * @return AuthenticationProvider that is responsible for user authentication.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Bean for configuring the AuthenticationManager, which is used to authenticate users.
     * The AuthenticationManager is a central interface for Spring Security's authentication mechanism.
     *
     * @param config The AuthenticationConfiguration provided by Spring Security.
     * @return AuthenticationManager to handle user authentication.
     * @throws Exception if the configuration fails.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean for password encoding, using BCryptPasswordEncoder.
     * BCrypt is a hashing function designed for secure password storage, which prevents rainbow table attacks.
     *
     * @return PasswordEncoder that uses BCrypt for password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
