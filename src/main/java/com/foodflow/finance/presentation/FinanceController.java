package com.foodflow.finance.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.finance.application.DashboardResponse;
import com.foodflow.finance.application.FinanceApplicationService;
import com.foodflow.finance.application.FinancialReportResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class FinanceController {

    private final FinanceApplicationService financeApplicationService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        DashboardResponse response = financeApplicationService.getDashboard(userAuth.getUserId());
        return ApiResponse.success(response);
    }

    @GetMapping("/reports")
    public ApiResponse<FinancialReportResponse> getFinancialReport(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @RequestParam(defaultValue = "DAILY") String period) {
        FinancialReportResponse response = financeApplicationService.getFinancialReport(userAuth.getUserId(), period);
        return ApiResponse.success(response);
    }
}
