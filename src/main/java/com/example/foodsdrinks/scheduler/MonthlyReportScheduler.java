package com.example.foodsdrinks.scheduler;

import com.example.foodsdrinks.service.MonthlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonthlyReportScheduler {

    private final MonthlyReportService monthlyReportService;

    @Scheduled(cron = "0 0 8 L * ?")
    public void run() {
        monthlyReportService.generateAndSend();
    }
}
