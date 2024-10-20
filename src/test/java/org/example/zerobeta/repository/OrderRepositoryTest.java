package org.example.zerobeta.repository;

import org.example.zerobeta.model.Client;
import org.example.zerobeta.model.Order;
import org.example.zerobeta.model.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")  // Activate the H2 in-memory test profile
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Client client;
    private Order order1, order2, order3;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();
        clientRepository.save(client);

        order1 = Order.builder()
                .itemName("Item A")
                .quantity(2)
                .shippingAddress("123 Street, City")
                .status(OrderStatus.NEW)
                .client(client)
                .build();

        order2 = Order.builder()
                .itemName("Item B")
                .quantity(1)
                .shippingAddress("456 Avenue, City")
                .status(OrderStatus.DISPATCHED)
                .client(client)
                .build();

        order3 = Order.builder()
                .itemName("Item C")
                .quantity(5)
                .shippingAddress("789 Boulevard, City")
                .status(OrderStatus.CANCELLED)
                .client(client)
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    void testFindByClientId() {
        Pageable pageable = PageRequest.of(0, 2);  // Fetch 2 records per page
        Page<Order> ordersPage = orderRepository.findByClientId(client.getId(), pageable);

        assertThat(ordersPage.getContent()).hasSize(2);
        assertThat(ordersPage.getContent().get(0).getItemName()).isEqualTo("Item A");
        assertThat(ordersPage.getContent().get(1).getItemName()).isEqualTo("Item B");

        assertThat(ordersPage.getTotalElements()).isEqualTo(3);
    }

    @Test
    void testFindByStatus() {
        List<Order> dispatchedOrders = orderRepository.findByStatus(OrderStatus.DISPATCHED);

        assertThat(dispatchedOrders).hasSize(1);
        assertThat(dispatchedOrders.get(0).getItemName()).isEqualTo("Item B");
        assertThat(dispatchedOrders.get(0).getStatus()).isEqualTo(OrderStatus.DISPATCHED);
    }

    @Test
    void testFindByStatus_NoResults() {
        List<Order> cancelledOrders = orderRepository.findByStatus(OrderStatus.CANCELLED);

        assertThat(cancelledOrders).hasSize(1);
        assertThat(cancelledOrders.get(0).getItemName()).isEqualTo("Item C");
        assertThat(cancelledOrders.get(0).getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}
