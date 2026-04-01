package com.foodflow.finance.presentation;

import com.foodflow.common.presentation.ApiResponse;
import com.foodflow.finance.application.DashboardResponse;
import com.foodflow.finance.application.FinanceApplicationService;
import com.foodflow.finance.application.FinancialReportResponse;
import com.foodflow.identity.infrastructure.UserAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
@Tag(name = "Finance & Reports", description = "APIs for financial dashboard and reporting")
@SecurityRequirement(name = "Bearer Authentication")
public class FinanceController {

    private final FinanceApplicationService financeApplicationService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get financial dashboard", description = "Retrieve dashboard metrics including income, expenses, profit, variations, and top 5 dishes")
    public ApiResponse<DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserAuthentication userAuth) {
        DashboardResponse response = financeApplicationService.getDashboard(userAuth.getUserId());
        return ApiResponse.success(response);
    }

    @GetMapping("/reports")
    @Operation(summary = "Get financial report", description = "Retrieve financial reports for DAILY, WEEKLY, or MONTHLY periods with comparison to previous period")
    public ApiResponse<FinancialReportResponse> getFinancialReport(
            @AuthenticationPrincipal UserAuthentication userAuth,
            @Parameter(description = "Report period: DAILY, WEEKLY, or MONTHLY", in = ParameterIn.QUERY, required = true)
            @RequestParam(defaultValue = "DAILY") String period) {
        FinancialReportResponse response = financeApplicationService.getFinancialReport(userAuth.getUserId(), period);
        return ApiResponse.success(response);
    }
}
