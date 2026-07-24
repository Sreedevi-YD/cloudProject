plugins {
    id("java")
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
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
