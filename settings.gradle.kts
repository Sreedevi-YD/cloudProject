plugins {
    // Lets Gradle auto-download a matching JDK when the host only has an older/newer one
    // installed (e.g. running this build where only JDK 16 is present) instead of failing
    // with "no matching toolchain found".
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "employee-onboarding-portal"

// gradle/libs.versions.toml is auto-detected by convention as the "libs" catalog —
// no explicit dependencyResolutionManagement block needed (declaring one double-registers it).

include(
    "onboarding-domain",
    "onboarding-application",
    "onboarding-infrastructure",
    "onboarding-presentation",
    "onboarding-bootstrap"
)
