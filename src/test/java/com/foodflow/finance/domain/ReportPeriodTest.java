package com.foodflow.finance.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReportPeriodTest {

    // Prueba unitaria: calcula ventana diaria de reportes (BE-UT-019)
    @Test
    void calculatesDailyWindow() {
        LocalDateTime reference = LocalDateTime.of(2026, 5, 9, 15, 30);

        assertThat(ReportPeriod.DAILY.getPeriodStart(reference))
                .isEqualTo(LocalDateTime.of(2026, 5, 9, 0, 0));
        assertThat(ReportPeriod.DAILY.getPeriodEnd(reference))
                .isEqualTo(LocalDateTime.of(2026, 5, 10, 0, 0));
    }
    // fin prueba

    // Prueba unitaria: calcula semana desde lunes hasta lunes siguiente (BE-UT-020)
    @Test
    void calculatesWeeklyWindowStartingOnMonday() {
        LocalDateTime reference = LocalDateTime.of(2026, 5, 9, 15, 30);

        assertThat(ReportPeriod.WEEKLY.getPeriodStart(reference))
                .isEqualTo(LocalDateTime.of(2026, 5, 4, 0, 0));
        assertThat(ReportPeriod.WEEKLY.getPeriodEnd(reference))
                .isEqualTo(LocalDateTime.of(2026, 5, 11, 0, 0));
    }
    // fin prueba

    // Prueba unitaria: calcula mes calendario completo (BE-UT-021)
    @Test
    void calculatesMonthlyWindow() {
        LocalDateTime reference = LocalDateTime.of(2026, 5, 9, 15, 30);

        assertThat(ReportPeriod.MONTHLY.getPeriodStart(reference))
                .isEqualTo(LocalDateTime.of(2026, 5, 1, 0, 0));
        assertThat(ReportPeriod.MONTHLY.getPeriodEnd(reference))
                .isEqualTo(LocalDateTime.of(2026, 6, 1, 0, 0));
    }
    // fin prueba
}
