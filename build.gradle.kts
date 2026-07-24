// Spring Boot's and Spring's Gradle plugins are resolved as plain Maven Central artifacts via
// buildscript classpath, not the `plugins { id(...) }` DSL. That DSL always resolves through the
// Gradle Plugin Portal (plugins.gradle.org) first, which some corporate networks block outright
// even when Maven Central and GitHub are reachable — buildscript classpath sidesteps it entirely.
// NOTE: keep these versions in sync with gradle/libs.versions.toml's springBoot /
// springDependencyManagement entries; they can't be read from the version catalog this early.
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:3.3.5")
        classpath("io.spring.gradle:dependency-management-plugin:1.1.6")
    }
}

plugins {
    id("java")
}

allprojects {
    group = "com.enterprise.onboarding"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // java-library (not plain java) so the `api(...)` configuration used to expose
    // domain/application types transitively between modules is actually available.
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }

    dependencies {
        "compileOnly"(rootProject.libs.lombok)
        "annotationProcessor"(rootProject.libs.lombok)
        "testImplementation"(rootProject.libs.spring.boot.starter.test)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
        // Targets Java 21 language/API level via cross-compilation (--release), without requiring
        // Gradle to locate or auto-provision an exact JDK 21 — any JDK 21+ launching Gradle works,
        // no toolchain resolution (and no Foojay plugin / plugin-portal access) needed.
        options.release.set(21)
    }
}
