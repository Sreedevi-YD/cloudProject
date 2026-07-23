# --- Build stage -------------------------------------------------------
# Uses the official Gradle image directly (no committed wrapper jar needed for the build).
# If you generate ./gradlew locally (gradle wrapper) you can switch this to eclipse-temurin
# + ./gradlew for reproducible offline builds; functionally equivalent.
FROM gradle:8.11-jdk21 AS build
WORKDIR /workspace

COPY settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY onboarding-domain/build.gradle.kts onboarding-domain/build.gradle.kts
COPY onboarding-application/build.gradle.kts onboarding-application/build.gradle.kts
COPY onboarding-infrastructure/build.gradle.kts onboarding-infrastructure/build.gradle.kts
COPY onboarding-presentation/build.gradle.kts onboarding-presentation/build.gradle.kts
COPY onboarding-bootstrap/build.gradle.kts onboarding-bootstrap/build.gradle.kts

COPY onboarding-domain onboarding-domain
COPY onboarding-application onboarding-application
COPY onboarding-infrastructure onboarding-infrastructure
COPY onboarding-presentation onboarding-presentation
COPY onboarding-bootstrap onboarding-bootstrap

RUN gradle :onboarding-bootstrap:bootJar --no-daemon -x test

# --- Runtime stage -------------------------------------------------------
FROM eclipse-temurin:21-jre AS runtime

RUN groupadd --gid 1000 onboarding && \
    useradd --uid 1000 --gid onboarding --shell /bin/false --create-home onboarding

WORKDIR /app
COPY --from=build /workspace/onboarding-bootstrap/build/libs/employee-onboarding-portal.jar app.jar

RUN mkdir -p /app/logs && chown -R onboarding:onboarding /app
USER onboarding

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
