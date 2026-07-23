package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.event.AssetAssignedEvent;
import com.enterprise.onboarding.application.event.DocumentUploadedEvent;
import com.enterprise.onboarding.application.event.OnboardingApprovedEvent;
import com.enterprise.onboarding.application.event.OnboardingCompletedEvent;
import com.enterprise.onboarding.application.event.OnboardingRejectedEvent;
import com.enterprise.onboarding.application.event.OnboardingRequestCreatedEvent;
import com.enterprise.onboarding.application.port.out.NotificationPort;
import com.enterprise.onboarding.application.port.out.NotificationPort.NotificationMessage;
import com.enterprise.onboarding.application.port.out.NotificationPort.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Translates workflow events into notifications. Decoupled from the delivery mechanism via
 * {@link NotificationPort} — today it's routed to the logging adapter, tomorrow an email/SES adapter.
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationPort notificationPort;

    @EventListener
    public void onCreated(OnboardingRequestCreatedEvent event) {
        notificationPort.notify(new NotificationMessage(
                event.hiringManagerId(), null,
                "New onboarding request awaiting your approval",
                "Onboarding request " + event.onboardingRequestId() + " requires your approval.",
                NotificationType.ONBOARDING_REQUEST_CREATED));
    }

    @EventListener
    public void onApproved(OnboardingApprovedEvent event) {
        notificationPort.notify(new NotificationMessage(
                null, null,
                "Onboarding request approved",
                "Onboarding request " + event.onboardingRequestId() + " was approved.",
                NotificationType.ONBOARDING_APPROVED));
    }

    @EventListener
    public void onRejected(OnboardingRejectedEvent event) {
        notificationPort.notify(new NotificationMessage(
                null, null,
                "Onboarding request rejected",
                "Onboarding request " + event.onboardingRequestId() + " was rejected: " + event.reason(),
                NotificationType.ONBOARDING_REJECTED));
    }

    @EventListener
    public void onDocumentUploaded(DocumentUploadedEvent event) {
        notificationPort.notify(new NotificationMessage(
                null, null,
                "Document uploaded",
                "A " + event.documentType() + " document was uploaded for request " + event.onboardingRequestId() + ".",
                NotificationType.DOCUMENT_UPLOADED));
    }

    @EventListener
    public void onAssetAssigned(AssetAssignedEvent event) {
        notificationPort.notify(new NotificationMessage(
                null, null,
                "Asset assigned",
                "A " + event.assetType() + " was assigned for request " + event.onboardingRequestId() + ".",
                NotificationType.ASSET_ASSIGNED));
    }

    @EventListener
    public void onCompleted(OnboardingCompletedEvent event) {
        notificationPort.notify(new NotificationMessage(
                null, null,
                "Onboarding completed",
                "Onboarding request " + event.onboardingRequestId() + " is now fully complete.",
                NotificationType.ONBOARDING_COMPLETED));
    }
}
