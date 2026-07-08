package com.foodflow.common.domain;

/**
 * Raised when a user tries to create a resource beyond the allowance of their
 * active subscription plan. The message deliberately mentions the plan limit so
 * the frontend can surface a "limit exceeded / upgrade your plan" message.
 */
public class PlanLimitExceededException extends DomainException {

    public PlanLimitExceededException(String resource, String planName, int limit) {
        super("PLAN_LIMIT_EXCEEDED",
                "You have reached your " + planName + " plan limit of " + limit + " " + resource
                        + ". Upgrade your subscription to add more.");
    }
}
