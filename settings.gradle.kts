pluginManagement {
    plugins {
        kotlin("jvm") version "1.5.21"
        antlr
        signing
        `maven-publish`
        `java-test-fixtures`
        id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
        id("org.jetbrains.dokka") version "1.5.0"
    }
}

rootProject.name = "impropriety"

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs.create("libs") {
        version("spek", "2.0.16")
        version("kotlin", "1.5.21")
        version("antlr", "4.9.2")

        alias("antlr-full").to("org.antlr", "antlr4").versionRef("antlr")
        alias("antlr-runtime").to("org.antlr", "antlr4-runtime").versionRef("antlr")

        alias("kotlin-test").to("org.jetbrains.kotlin", "kotlin-test").versionRef("kotlin")
        alias("spek-dsl").to("org.spekframework.spek2", "spek-dsl-jvm").versionRef("spek")

        alias("spek-runner").to("org.spekframework.spek2", "spek-runner-junit5").versionRef("spek")
        alias("spek-runtime").to("org.spekframework.spek2", "spek-runtime-jvm").versionRef("spek")

        alias("kotlin-reflect").to("org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")

        alias("jackson-kotlin").to("com.fasterxml.jackson.module", "jackson-module-kotlin").version("2.13.+")

        bundle("testing-core", listOf("spek-dsl", "kotlin-test"))
        bundle("testing-runtime", listOf("spek-runner", "spek-runtime", "kotlin-reflect"))
    }
}