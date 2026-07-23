# Employee Onboarding Portal

Enterprise onboarding workflow (HR → Manager → Employee → IT → completion) built as a Gradle
multi-module, Clean Architecture Spring Boot 3 application on Java 21, backed by a single
Microsoft SQL Server database and MinIO for document storage. Designed to run on-premises first
(Docker / Kubernetes) and migrate to AWS later via configuration only.

## Architecture

Four layers, one Gradle module each, dependencies pointing inward only:

```
onboarding-domain          <- entities, enums, repository PORTS, domain exceptions. No frameworks.
        ^
onboarding-application     <- use cases (services), DTOs, MapStruct mappers, events, out-ports.
        ^
onboarding-infrastructure  <- JPA adapters, Spring Security/JWT, MinIO adapter, AOP audit trail.
onboarding-presentation    <- REST controllers, global exception handling.
        ^ (both infrastructure and presentation depend only on application)
onboarding-bootstrap       <- Spring Boot main class, application-*.yml, Flyway migrations.
                               The only module that depends on all four and produces a runnable jar.
```

`onboarding-infrastructure` and `onboarding-presentation` never depend on each other — only
`onboarding-bootstrap` wires them together at runtime via Spring component scanning. Swapping an
adapter (e.g. MinIO → S3, or the JWT filter → Spring Session) touches infrastructure only.

## Workflow → code map

| Step | Actor | Endpoint | Code |
|---|---|---|---|
| 1. Create request | HR | `POST /api/v1/onboarding-requests` | `OnboardingWorkflowServiceImpl.createRequest` |
| 2. Approve/reject | Manager | `POST /api/v1/onboarding-requests/{id}/approve|reject` | `OnboardingRequest.approve/reject` (domain state machine) |
| 3. Upload documents | Employee | `POST /api/v1/documents` | `DocumentServiceImpl.upload` → MinIO |
| 4. Allocate assets/tasks | IT / auto | `POST /api/v1/assets`, auto-created tasks | `OnboardingTaskAutoCreationListener` (fires on `OnboardingApprovedEvent`) |
| 5. Complete tasks | IT / HR | `PATCH /api/v1/tasks/{id}/status` | `TaskServiceImpl.updateStatus` |
| 6. Mark completed | System | (automatic) | `OnboardingWorkflowServiceImpl.completeIfAllTasksDone`, triggered once every task is `COMPLETED` |

Every `@Auditable`-annotated use case is recorded to `audit_log` by `AuditAspect` (Spring AOP) —
no manual audit-writing code inside services. Notifications are event-driven
(`NotificationEventListener` → `NotificationPort`); today only a logging adapter exists, so
plugging in email/SES later means adding one new adapter class, not touching any use case.

## Roles (RBAC)

`ROLE_ADMIN`, `ROLE_HR`, `ROLE_MANAGER`, `ROLE_IT`, `ROLE_EMPLOYEE` — enforced both at the HTTP
layer (`SecurityConfig` request matchers) and per-method (`@PreAuthorize` on controllers), backed
by JWT bearer tokens (`onboarding-infrastructure/.../security`).

## Running locally

**Prerequisites:** JDK 21, Docker Desktop. Gradle is not vendored as a wrapper in this scaffold —
either install Gradle 8.11+ locally and run `gradle wrapper` once to generate `gradlew`, or use
the Docker build below, which pulls the official `gradle:8.11-jdk21` image and needs nothing
installed beyond Docker.

### Full stack via Docker Compose (SQL Server + MinIO + app)

```bash
docker compose up --build
```

- App: http://localhost:8080 (Swagger UI: `/swagger-ui.html`)
- MinIO console: http://localhost:9001 (minioadmin/minioadmin by default)
- A default `dev`-profile admin user (`admin` / `Admin@12345`) is seeded automatically by
  `DevDataSeeder` — **remove or gate this behind a stronger mechanism before any shared/prod use.**

### Build the jar directly

```bash
gradle :onboarding-bootstrap:bootJar
java -jar onboarding-bootstrap/build/libs/employee-onboarding-portal.jar
```

## Kubernetes (on-prem)

```bash
kubectl apply -k k8s/
```

Includes: namespace, ConfigMap/Secret, a single-instance SQL Server `StatefulSet` (one database,
as specified), a MinIO `Deployment` + PVC, the app `Deployment`/`Service`/`HPA`, and an `Ingress`
that exposes the portal (and its Swagger UI) as a web page at the configured host. Build and push
the app image first:

```bash
docker build -t <your-registry>/employee-onboarding-portal:latest .
docker push <your-registry>/employee-onboarding-portal:latest
# then update the image in k8s/20-app-deployment.yaml
```

`k8s/02-secret.yaml` ships with placeholder values — replace them (or manage the Secret via your
usual mechanism: sealed-secrets, external-secrets-operator, Vault) before applying to a real
cluster.

## AWS migration path (minimal code change by design)

The two external dependencies are abstracted behind ports for exactly this reason:

- **Database** — `FileStoragePort`'s sibling, the JPA layer, talks to SQL Server through the
  standard `mssql-jdbc` driver. Pointing `DB_URL` at Amazon RDS for SQL Server (or SQL Server on
  EC2) is a connection-string change; same driver, same Flyway migrations
  (`onboarding-bootstrap/src/main/resources/db/migration`), same JPA entities.
- **Object storage** — `FileStoragePort` is implemented today by `MinioFileStorageAdapter`, but
  the MinIO Java SDK is itself an S3-compatible client. Pointing `MINIO_ENDPOINT` at
  `https://s3.<region>.amazonaws.com` with an IAM access key/secret works with the *same adapter
  class* — no new code required. If/when you want native `software.amazon.awssdk:s3` instead, only
  `onboarding-infrastructure/.../storage` changes; the `FileStoragePort` contract and every caller
  stay untouched.
- **Secrets** — `application-aws.yml` documents the env vars this profile expects; wire them from
  AWS Secrets Manager (via the External Secrets Operator or the Secrets Manager CSI driver) into
  the same variable names rather than hardcoding values.
- **Compute** — the same container image runs on EKS; `k8s/` manifests are standard Kubernetes and
  do not use any on-prem-specific APIs, so `kubectl apply -k k8s/` against an EKS context is the
  starting point, refined with an ALB Ingress Controller and IRSA for the S3/Secrets Manager
  permissions instead of static keys.

## CI/CD (GitHub Actions)

Two workflows live in `.github/workflows/`:

- **`ci.yml`** — runs on every push/PR to `main`/`develop`: provisions Gradle 8.11 (no wrapper is
  committed to this repo) and runs `gradle build` across all modules.
- **`cd.yml`** — runs on every push to `main`: repeats the build/test, builds the Docker image and
  pushes it to GHCR (`ghcr.io/<owner>/<repo>`, authenticated with the built-in `GITHUB_TOKEN` — no
  registry secret needed), then applies `k8s/` and rolls the deployment to the new image via
  `kubectl set image`.

### One-time setup

1. **Create the GitHub repo** (empty, no README/license so it doesn't conflict with this one):
   ```bash
   gh repo create <your-org-or-user>/employee-onboarding-portal --private --source=. --remote=origin
   # or, without gh: create it on github.com, then:
   git remote add origin https://github.com/<your-org-or-user>/employee-onboarding-portal.git
   ```

2. **Push:**
   ```bash
   git push -u origin main
   ```
   `ci.yml` will run immediately on this push. `cd.yml` will also run (it triggers on push to
   `main`) — its `deploy` job will fail until step 3 is done, which is expected.

3. **Add the `KUBE_CONFIG` secret** so the `deploy` job in `cd.yml` can reach your cluster:
   ```bash
   kubectl config view --raw --minify --flatten -context <your-context> | base64 -w0 > kubeconfig.b64
   gh secret set KUBE_CONFIG --repo <your-org-or-user>/employee-onboarding-portal < kubeconfig.b64
   rm kubeconfig.b64
   ```
   Or paste the base64 output into **Settings → Secrets and variables → Actions → New repository
   secret** on GitHub if you're not using `gh`. Use a service-account kubeconfig scoped to the
   `onboarding-portal` namespace, not your personal admin credentials.

4. (Recommended) In **Settings → Environments**, create a `production` environment matching the
   `deploy` job and add required reviewers, so every rollout needs manual approval.

5. **Package visibility**: the first push makes the GHCR package private by default under most
   org settings; if your cluster's `kubectl`/kubelet needs to pull it, either make the package
   public (Package settings on GitHub) or create an `imagePullSecret` in the `onboarding-portal`
   namespace and reference it from `k8s/20-app-deployment.yaml`.

## Module map (Gradle)

```
settings.gradle.kts
build.gradle.kts                 # shared subproject config (Java 21 toolchain, Spring BOM import)
gradle/libs.versions.toml        # centralized dependency versions
onboarding-domain/
onboarding-application/
onboarding-infrastructure/
onboarding-presentation/
onboarding-bootstrap/            # OnboardingPortalApplication + application-*.yml + Flyway SQL
Dockerfile
docker-compose.yml
docker/sqlserver/init-db.sql     # creates the onboarding_portal database on a fresh container
k8s/                             # kustomize-able manifest set
```

## Known gaps / next steps

- `POST /api/v1/auth/register` is currently `permitAll` alongside `/login` for scaffold
  convenience; in a real deployment, restrict user creation to `ROLE_ADMIN` (or a separate
  provisioning flow) once the default admin exists.
- No frontend module is included — the spec listed REST APIs only. Swagger UI
  (`/swagger-ui.html`) is the browsable entry point; a separate SPA can consume the same API.
- Email delivery is not implemented; `NotificationPort` is ready for an SMTP or SES adapter.
- Integration tests are not included; `onboarding-infrastructure` already pulls in Testcontainers
  (`mssqlserver` module) for when they're added.
