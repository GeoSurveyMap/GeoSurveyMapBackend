import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.dsl.ImportsHandler
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.allopen") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
}

group = "com.loess"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

ext {
    set("geoSurveyMapVersion", "1.0.0")
}

allprojects {
    group = "com.loess.geosurveymap"
    version = rootProject.ext.get("geoSurveyMapVersion") as String
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.hibernate:hibernate-spatial:6.1.3.Final")
    implementation("com.bedatadriven:jackson-datatype-jts:2.4")
    implementation("org.locationtech.jts:jts-core:1.18.2")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springDocVersion")}")
    implementation("org.springdoc:springdoc-openapi-starter-common:${property("springDocVersion")}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:${property("swaggerAnnotationVersion")}")
    implementation("io.github.oshai:kotlin-logging:${property("kotlinLoggingVersion")}")

    implementation("io.micrometer:micrometer-observation")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}

configure(allprojects.filter {
    it.path !in setOf(
        ":modules",
        ":modules:mobile-frontend",
        ":modules:service-modules",
    )
}) {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    configure<AllOpenExtension> {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.MappedSuperclass")
    }

    repositories { mavenCentral() }

    configure<DependencyManagementExtension> {
        imports(delegateClosureOf<ImportsHandler> {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${property("springBootVersion")}")
        })
    }

    tasks.withType<JavaCompile>() {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }

    tasks.withType<Test>() {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = JavaVersion.VERSION_21.toString()
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
