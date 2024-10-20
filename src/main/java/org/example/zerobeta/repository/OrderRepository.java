package org.example.zerobeta.repository;

import org.example.zerobeta.model.Order;
import org.example.zerobeta.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findByClientId(Long clientId, Pageable pageable);
    List<Order> findByStatus(OrderStatus status);
}
