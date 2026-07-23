-- Employee Onboarding Portal - initial schema (Microsoft SQL Server)

CREATE TABLE app_user (
    id                  UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    username            NVARCHAR(100)    NOT NULL,
    email               NVARCHAR(255)    NOT NULL,
    password_hash       NVARCHAR(255)    NOT NULL,
    enabled             BIT              NOT NULL DEFAULT 1,
    employee_id         UNIQUEIDENTIFIER NULL,
    created_at          DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at          DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT uq_app_user_username UNIQUE (username)
);

CREATE TABLE user_role (
    user_id             UNIQUEIDENTIFIER NOT NULL,
    role                NVARCHAR(30)     NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE TABLE employee (
    id                  UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    employee_code       NVARCHAR(40)     NOT NULL,
    first_name          NVARCHAR(100)    NOT NULL,
    last_name           NVARCHAR(100)    NOT NULL,
    personal_email      NVARCHAR(255)    NULL,
    work_email          NVARCHAR(255)    NOT NULL,
    phone_number        NVARCHAR(30)     NULL,
    designation         NVARCHAR(150)    NULL,
    department          NVARCHAR(100)    NULL,
    manager_id          UNIQUEIDENTIFIER NULL,
    date_of_joining     DATE             NULL,
    active              BIT              NOT NULL DEFAULT 1,
    created_at          DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at          DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT uq_employee_code UNIQUE (employee_code),
    CONSTRAINT uq_employee_work_email UNIQUE (work_email),
    CONSTRAINT fk_employee_manager FOREIGN KEY (manager_id) REFERENCES employee (id)
);

ALTER TABLE app_user
    ADD CONSTRAINT fk_app_user_employee FOREIGN KEY (employee_id) REFERENCES employee (id);

CREATE TABLE onboarding_request (
    id                      UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    candidate_name          NVARCHAR(200)    NOT NULL,
    candidate_email         NVARCHAR(255)    NOT NULL,
    designation             NVARCHAR(150)    NULL,
    department              NVARCHAR(100)    NULL,
    proposed_joining_date   DATE             NULL,
    created_by_user_id      UNIQUEIDENTIFIER NULL,
    hiring_manager_id       UNIQUEIDENTIFIER NULL,
    employee_id             UNIQUEIDENTIFIER NULL,
    status                  NVARCHAR(30)     NOT NULL,
    rejection_reason        NVARCHAR(1000)   NULL,
    created_at              DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at              DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    approved_at             DATETIME2        NULL,
    completed_at            DATETIME2        NULL,
    CONSTRAINT fk_onboarding_request_employee FOREIGN KEY (employee_id) REFERENCES employee (id)
);
CREATE INDEX ix_onboarding_request_status ON onboarding_request (status);
CREATE INDEX ix_onboarding_request_manager ON onboarding_request (hiring_manager_id);

CREATE TABLE document (
    id                      UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    onboarding_request_id   UNIQUEIDENTIFIER NOT NULL,
    employee_id             UNIQUEIDENTIFIER NULL,
    document_type           NVARCHAR(40)     NOT NULL,
    file_name               NVARCHAR(255)    NOT NULL,
    content_type            NVARCHAR(150)    NULL,
    file_size_bytes         BIGINT           NOT NULL DEFAULT 0,
    storage_bucket          NVARCHAR(100)    NOT NULL,
    storage_key             NVARCHAR(500)    NOT NULL,
    checksum                NVARCHAR(100)    NULL,
    uploaded_by_user_id     UNIQUEIDENTIFIER NULL,
    uploaded_at             DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT fk_document_request FOREIGN KEY (onboarding_request_id) REFERENCES onboarding_request (id)
);
CREATE INDEX ix_document_request ON document (onboarding_request_id);

CREATE TABLE asset (
    id                      UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    onboarding_request_id   UNIQUEIDENTIFIER NOT NULL,
    employee_id             UNIQUEIDENTIFIER NULL,
    asset_type              NVARCHAR(30)     NOT NULL,
    asset_tag               NVARCHAR(100)    NULL,
    status                  NVARCHAR(30)     NOT NULL,
    assigned_by_user_id     UNIQUEIDENTIFIER NULL,
    assigned_at             DATETIME2        NULL,
    returned_at             DATETIME2        NULL,
    CONSTRAINT fk_asset_request FOREIGN KEY (onboarding_request_id) REFERENCES onboarding_request (id)
);
CREATE INDEX ix_asset_request ON asset (onboarding_request_id);

CREATE TABLE onboarding_task (
    id                      UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    onboarding_request_id   UNIQUEIDENTIFIER NOT NULL,
    title                   NVARCHAR(200)    NOT NULL,
    description             NVARCHAR(1000)   NULL,
    owning_department       NVARCHAR(30)     NOT NULL,
    assigned_to_user_id     UNIQUEIDENTIFIER NULL,
    status                  NVARCHAR(30)     NOT NULL,
    created_at              DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at              DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
    completed_at            DATETIME2        NULL,
    CONSTRAINT fk_task_request FOREIGN KEY (onboarding_request_id) REFERENCES onboarding_request (id)
);
CREATE INDEX ix_task_request ON onboarding_task (onboarding_request_id);
CREATE INDEX ix_task_department ON onboarding_task (owning_department);
CREATE INDEX ix_task_assignee ON onboarding_task (assigned_to_user_id);

CREATE TABLE audit_log (
    id                      UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    entity_type             NVARCHAR(100)    NOT NULL,
    entity_id               UNIQUEIDENTIFIER NULL,
    action                  NVARCHAR(30)     NOT NULL,
    performed_by_user_id    UNIQUEIDENTIFIER NULL,
    details                 NVARCHAR(2000)   NULL,
    occurred_at             DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME()
);
CREATE INDEX ix_audit_log_entity ON audit_log (entity_type, entity_id);
CREATE INDEX ix_audit_log_occurred_at ON audit_log (occurred_at DESC);
