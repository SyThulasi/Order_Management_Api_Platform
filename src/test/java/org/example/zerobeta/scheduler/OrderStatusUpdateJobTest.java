package org.example.zerobeta.scheduler;

import org.example.zerobeta.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class OrderStatusUpdateJobTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderStatusUpdateJob orderStatusUpdateJob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateOrders() {
        orderStatusUpdateJob.updateOrders();

        verify(orderService, times(1)).updateNewOrdersToDispatched();
    }
}
