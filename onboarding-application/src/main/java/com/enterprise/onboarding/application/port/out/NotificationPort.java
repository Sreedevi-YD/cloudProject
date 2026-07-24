package com.enterprise.onboarding.application.port.out;

import java.util.UUID;

/**
 * Outbound notification port. Only a logging/no-op adapter exists today; the interface is
 * shaped so an email (or SNS, on AWS) adapter can be dropped in later without touching callers.
 */
public interface NotificationPort {

    void notify(NotificationMessage message);

    record NotificationMessage(
            UUID recipientUserId,
            String recipientEmail,
            String subject,
            String body,
            NotificationType type
    ) {
    }

    enum NotificationType {
        ONBOARDING_REQUEST_CREATED,
        ONBOARDING_APPROVED,
        ONBOARDING_REJECTED,
        DOCUMENT_UPLOADED,
        ASSET_ASSIGNED,
        TASK_ASSIGNED,
        ONBOARDING_COMPLETED
    }
}
