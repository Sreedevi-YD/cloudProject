// Use-case orchestration layer. Depends on domain only; no web, no JPA, no MinIO SDK here.
// Spring's context/tx annotations are allowed pragmatically (DI + @Transactional + events),
// but no spring-boot-starter-web/data-jpa may be introduced in this module.
dependencies {
    api(project(":onboarding-domain"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.lombok.mapstruct.binding)
}
