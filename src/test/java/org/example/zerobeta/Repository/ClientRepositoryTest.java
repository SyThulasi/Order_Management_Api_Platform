package org.example.zerobeta.Repository;

import org.example.zerobeta.Model.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client client;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();
        clientRepository.save(client);
    }

    @AfterEach
    void tearDown() {
        clientRepository.deleteAll();
    }

    @Test
    void testFindByEmail() {
        Optional<Client> foundClient = clientRepository.findByEmail("john.doe@example.com");

        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundClient.get().getFirstName()).isEqualTo("John");
        assertThat(foundClient.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<Client> foundClient = clientRepository.findByEmail("notfound@example.com");

        assertThat(foundClient).isNotPresent();
    }

    @Test
    void testClientUserDetails() {
        Optional<Client> foundClient = clientRepository.findByEmail("john.doe@example.com");

        assertThat(foundClient).isPresent();
        Client clientUserDetails = foundClient.get();

        assertThat(clientUserDetails.getAuthorities()).hasSize(1);
        assertThat(clientUserDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        assertThat(clientUserDetails.getPassword()).isEqualTo("password123");
        assertThat(clientUserDetails.getUsername()).isEqualTo("john.doe@example.com");
        assertThat(clientUserDetails.isAccountNonExpired()).isTrue();
        assertThat(clientUserDetails.isAccountNonLocked()).isTrue();
        assertThat(clientUserDetails.isCredentialsNonExpired()).isTrue();
        assertThat(clientUserDetails.isEnabled()).isTrue();
    }
}
