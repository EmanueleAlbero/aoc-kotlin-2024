plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.aoc2024"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}