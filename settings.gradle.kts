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
