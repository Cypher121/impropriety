import java.net.URL

plugins {
    kotlin("jvm")
    antlr
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
    id("org.jetbrains.dokka")
    `java-test-fixtures`
}

group = "coffee.cypher.impropriety"
version = "1.0.1"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    modules {
        module("spek:spek-runtime") {
            replacedBy(libs.spek.runtime.get().module, "Spek has new central coordinates")
        }

        module("spek:spek-dsl") {
            replacedBy(libs.spek.dsl.get().module, "Spek has new central coordinates")
        }
    }

    antlr(libs.antlr.full)
    implementation(libs.antlr.runtime)
    implementation(kotlin("stdlib"))

    testImplementation(libs.bundles.testing.core)
    testRuntimeOnly(libs.bundles.testing.runtime)

    testFixturesImplementation(libs.jackson.kotlin)
}

configurations.api {
    setExtendsFrom(extendsFrom.filterNot { it.name == "antlr" })
}

sourceSets.all {
    java {
        setSrcDirs(srcDirs.filterNot { "generated-src" in it.path })
        srcDir(layout.buildDirectory.dir("generated/sources/antlr/${this@all.name}"))
    }
}

tasks {
    val relocateSources = register<Copy>("relocateSources") {
        dependsOn(generateGrammarSource)

        from(layout.buildDirectory.dir("generated-src/antlr"))
        into(layout.buildDirectory.dir("generated/sources/antlr"))

        filter {
            it.replace("public class", "class")
                .replace("public interface", "interface")
        }
    }

    //use kotlin versions instead!
    named("javadocJar") {
        dependsOn(dokkaJavadoc)
    }

    javadoc {
        enabled = false
    }

    named("sourcesJar") {
        dependsOn(relocateSources)
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        dependsOn(relocateSources)
    }

    generateGrammarSource {
        arguments = listOf("-visitor")
    }

    dokkaJavadoc {
        dependsOn(relocateSources)

        dokkaSourceSets.named("main") {
            reportUndocumented.set(true)

            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteLineSuffix.set("#L")
                remoteUrl.set(URL("https://github.com/Cypher121/impropriety/blob/master/src/main/kotlin"))
            }
        }
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "7.2-rc-2"
    }

    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}

dependencyLocking.lockAllConfigurations()

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            suppressAllPomMetadataWarnings()

            pom {
                name.set("Impropriety")
                description.set("Improperties parser for JVM")
                url.set("https://github.com/Cypher121/impropriety/")

                scm {
                    connection.set("scm:git:git://github.com/Cypher121/impropriety.git")
                    developerConnection.set("scm:git:ssh://github.com/Cypher121/impropriety.git")
                    url.set("https://github.com/Cypher121/impropriety/")
                }

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("cypher121")
                        name.set("Cypher121")
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

signing {
    sign(publishing.publications["maven"])
}