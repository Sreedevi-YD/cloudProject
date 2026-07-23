package com.enterprise.onboarding.infrastructure.notification;

import com.enterprise.onboarding.application.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default {@link NotificationPort} implementation: logs instead of sending. Replace with an
 * SmtpNotificationAdapter (on-prem) or SesNotificationAdapter (AWS) when email is required —
 * the port contract does not change.
 */
@Component
@Slf4j
public class LoggingNotificationAdapter implements NotificationPort {

    @Override
    public void notify(NotificationMessage message) {
        log.info("[NOTIFICATION] type={} recipientUserId={} recipientEmail={} subject=\"{}\" body=\"{}\"",
                message.type(), message.recipientUserId(), message.recipientEmail(), message.subject(), message.body());
    }
}
