package com.foodflow.finance.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public enum ReportPeriod {

    DAILY(1),
    WEEKLY(7),
    MONTHLY(30);

    private final int days;

    ReportPeriod(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

    public LocalDateTime getPeriodStart(LocalDateTime referenceDate) {
        LocalDate date = referenceDate.toLocalDate();
        return switch (this) {
            case DAILY -> date.atStartOfDay();
            case WEEKLY -> date.minusDays(date.getDayOfWeek().getValue() - 1).atStartOfDay();
            case MONTHLY -> date.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        };
    }

    public LocalDateTime getPeriodEnd(LocalDateTime referenceDate) {
        return switch (this) {
            case DAILY -> referenceDate.toLocalDate().plusDays(1).atStartOfDay();
            case WEEKLY -> referenceDate.toLocalDate().plusDays(8 - referenceDate.getDayOfWeek().getValue()).atStartOfDay();
            case MONTHLY -> referenceDate.toLocalDate().with(TemporalAdjusters.firstDayOfNextMonth()).atStartOfDay();
        };
    }

    public LocalDateTime getPreviousPeriodStart(LocalDateTime referenceDate) {
        return switch (this) {
            case DAILY -> referenceDate.minusDays(1).toLocalDate().atStartOfDay();
            case WEEKLY -> referenceDate.minusWeeks(1).toLocalDate().atStartOfDay();
            case MONTHLY -> referenceDate.minusMonths(1).toLocalDate().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        };
    }

    public LocalDateTime getPreviousPeriodEnd(LocalDateTime referenceDate) {
        return getPeriodStart(referenceDate);
    }
}
