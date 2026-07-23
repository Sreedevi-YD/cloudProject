// REST layer. Depends only on the application module's use-case ports and DTOs —
// it must never import JPA, MinIO, or JWT classes directly; those are infrastructure concerns.
dependencies {
    api(project(":onboarding-application"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi)
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.security:spring-security-web")
}
