import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.6"
    application
    checkstyle
    jacoco
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

application { mainClass.set("hexlet.code.App") }

dependencies {
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    implementation("org.apache.commons:commons-text:1.11.0")
    implementation("gg.jte:jte:3.1.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("io.javalin:javalin:6.1.3")
    implementation("io.javalin:javalin-bundle:6.1.3") {
        exclude(group = "ch.qos.logback")
    }
    implementation("io.javalin:javalin-rendering:6.1.3")

    implementation("com.konghq:unirest-java-bom:4.4.4")
    compileOnly("com.konghq:unirest-java-core:4.4.4")
    testImplementation("com.konghq:unirest-modules-gson:4.4.4")

    implementation("org.jsoup:jsoup:1.15.3")


    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.junit.jupiter:junit-jupiter")
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