package com.example.retailpricetracker.scheduler;

import com.example.retailpricetracker.service.PriceWatchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceWatchScheduler {

    private final PriceWatchService priceWatchService;

    public PriceWatchScheduler(PriceWatchService priceWatchService) {
        this.priceWatchService = priceWatchService;
    }

    @Scheduled(fixedRate = 60000) // Runs every hour (3600000 milliseconds)
    public void checkPriceDrops() {
        priceWatchService.monitorPriceDrops();
    }
}

