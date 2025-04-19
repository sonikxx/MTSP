
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1"
}

ktlint {
    android.set(false)
    outputColorName.set("RED")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // database staff start
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.postgresql:postgresql")
    // database migration staff
    implementation("org.flywaydb:flyway-database-postgresql")
    // kafka start
    implementation("org.springframework.kafka:spring-kafka")
    // kafka json parser
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // password hashing
    implementation("org.springframework.security:spring-security-crypto")
    // html template engine
    // implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    // other start
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // jwt staff
    implementation("io.jsonwebtoken:jjwt:0.12.3")
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    // spring data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
