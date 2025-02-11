import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
    kotlin("plugin.jpa") version "1.9.21"
}

group = "my.kotlin"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

val jsoupVersion = "1.17.1"
val springdocVersion = "2.2.0"
val kotlinLoggingVersion = "5.1.1"
val jacksonDatatypeVersion = "2.16.0"
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5-jakarta:$jacksonDatatypeVersion")
    implementation("com.h2database:h2")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    kotlinOptions {
        freeCompilerArgs += listOf("-Xjsr305=strict", "-Xjvm-default=all")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
