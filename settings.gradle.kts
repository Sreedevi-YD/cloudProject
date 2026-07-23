rootProject.name = "employee-onboarding-portal"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

include(
    "onboarding-domain",
    "onboarding-application",
    "onboarding-infrastructure",
    "onboarding-presentation",
    "onboarding-bootstrap"
)
