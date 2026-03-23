package com.example.foodsdrinks.controller.web;

import com.example.foodsdrinks.dto.response.DashboardStatsResponse;
import com.example.foodsdrinks.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public ResponseEntity<DashboardStatsResponse> stats() {
        log.info("Fetching dashboard stats");
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
