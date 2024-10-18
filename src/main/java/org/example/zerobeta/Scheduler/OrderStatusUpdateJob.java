package org.example.zerobeta.Scheduler;

import lombok.RequiredArgsConstructor;
import org.example.zerobeta.Service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusUpdateJob {

    private final OrderService orderService;

    // Scheduled to run every hour using cron expression
    @Scheduled(cron = "0 0 * * * *")  // Runs at the start of every hour
    public void updateOrders() {
        orderService.updateNewOrdersToDispatched();
    }
}
