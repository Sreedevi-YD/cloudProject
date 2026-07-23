// Adapters: JPA persistence, Spring Security/JWT, MinIO storage, AOP audit trail.
// Implements the ports declared in onboarding-application; nothing outside this module
// should import com.microsoft.sqlserver, io.minio, or io.jsonwebtoken directly.
dependencies {
    api(project(":onboarding-application"))

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.aop)

    implementation(libs.mssql.jdbc)
    implementation(libs.flyway.core)
    implementation(libs.flyway.sqlserver)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    implementation(libs.minio)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.lombok.mapstruct.binding)

    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.mssqlserver)
}
