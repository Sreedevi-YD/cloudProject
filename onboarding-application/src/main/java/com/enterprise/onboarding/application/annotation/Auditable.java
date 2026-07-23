package com.enterprise.onboarding.application.annotation;

import com.enterprise.onboarding.domain.model.AuditAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a use-case method whose outcome must be recorded to the audit trail.
 * Woven by an AOP aspect in the infrastructure layer; the application layer only declares intent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
    AuditAction action();
    String entityType();
}
