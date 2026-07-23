import org.springframework.boot.gradle.tasks.bundling.BootJar

// The only module that produces a runnable artifact. Wires domain + application + infrastructure
// + presentation together and owns environment configuration (application-*.yml).
plugins {
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":onboarding-domain"))
    implementation(project(":onboarding-application"))
    implementation(project(":onboarding-infrastructure"))
    implementation(project(":onboarding-presentation"))

    implementation(libs.spring.boot.starter.actuator)
    annotationProcessor(libs.spring.boot.configuration.processor)
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("employee-onboarding-portal.jar")
    mainClass.set("com.enterprise.onboarding.OnboardingPortalApplication")
}

tasks.named("jar") {
    enabled = false
}
