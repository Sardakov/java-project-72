import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    checkstyle
    jacoco
    id("io.freefair.lombok") version "8.6"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("hexlet.code.App")
}

dependencies {
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("org.apache.commons:commons-text:1.11.0")
    implementation("gg.jte:jte:3.1.9")
    implementation("org.slf4j:slf4j-simple:2.0.16") // Оставляем только последнюю версию
    implementation("io.javalin:javalin:6.1.3")
    implementation("io.javalin:javalin-bundle:6.1.3") {
        exclude(group = "ch.qos.logback")
    }
    implementation("io.javalin:javalin-rendering:6.1.3")
    implementation("com.konghq:unirest-java:3.13.6")
    implementation("com.konghq:unirest-java:3.11.09")
    implementation("org.jsoup:jsoup:1.15.3")
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("com.h2database:h2:2.1.224")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.slf4j:slf4j-api:2.0.16") // Удалите старую версию, оставьте только одну
}

tasks.test {
    useJUnitPlatform()
    // https://technology.lastminute.com/junit5-kotlin-and-gradle-dsl/
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        showStackTraces = true
        // showCauses = true
        showStandardStreams = true
    }
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }