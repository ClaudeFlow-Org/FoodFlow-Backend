package com.foodflow.billing.domain;

import com.foodflow.common.domain.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubscriptionDomainTest {

    // Prueba unitaria: considera activa suscripcion vigente (BE-UT-015)
    @Test
    void activeSubscriptionWithFutureEndDateIsActive() {
        Subscription subscription = Subscription.builder()
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .plan(SubscriptionPlan.STANDARD)
                .endDate(LocalDateTime.now().plusDays(5))
                .build();

        assertThat(subscription.isActive()).isTrue();
    }
    // fin prueba

    // Prueba unitaria: cancela suscripcion activa y registra fecha (BE-UT-016)
    @Test
    void cancelsActiveSubscription() {
        Subscription subscription = Subscription.builder()
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .plan(SubscriptionPlan.PREMIUM)
                .build();

        subscription.cancel();

        assertThat(subscription.getStatus()).isEqualTo(Subscription.SubscriptionStatus.CANCELLED);
        assertThat(subscription.getCancellationDate()).isNotNull();
        assertThat(subscription.isActive()).isFalse();
    }
    // fin prueba

    // Prueba unitaria: impide cancelar dos veces la suscripcion (BE-UT-017)
    @Test
    void rejectsCancellingAlreadyCancelledSubscription() {
        Subscription subscription = Subscription.builder()
                .status(Subscription.SubscriptionStatus.CANCELLED)
                .plan(SubscriptionPlan.PREMIUM)
                .build();

        assertThatThrownBy(subscription::cancel)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Subscription is already cancelled");
    }
    // fin prueba

    // Prueba unitaria: cambia plan de suscripcion existente (BE-UT-018)
    @Test
    void changesPlan() {
        Subscription subscription = Subscription.builder()
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .plan(SubscriptionPlan.FREE)
                .build();

        subscription.changePlan(SubscriptionPlan.PREMIUM);

        assertThat(subscription.getPlan()).isEqualTo(SubscriptionPlan.PREMIUM);
    }
    // fin prueba
}
