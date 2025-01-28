import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    kotlin("multiplatform") version "2.1.10"

    `maven-publish`
}

val v = "0.1.1"

group = "xyz.calcugames"
version = if (project.hasProperty("snapshot")) "$v-SNAPSHOT" else v
description = "The official CLI for the LevelZ File Format"

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.calcugames.xyz/repository/maven-public/")
}

dependencies {
    commonMainImplementation("com.github.ajalt.clikt:clikt:5.0.2")
    commonMainImplementation("xyz.calcugames:levelz-kt:0.3.3")
    commonMainImplementation("com.soywiz:korlibs-io:6.0.1")

    commonTestImplementation(kotlin("test"))
}

kotlin {
    listOf(
        mingwX64(),
        macosX64(),
        macosArm64(),
        linuxArm64(),
        linuxX64()
    ).forEach { build ->
        build.binaries {
            executable(
                listOf(NativeBuildType.RELEASE)
            ) {
                baseName = "levelz"

                entryPoint("xyz.calcugames.levelz.cli.main")
            }
        }
    }
}

tasks {
    register("copyTestResources", Copy::class) {
        from("src/commonTest/resources")
        into(layout.buildDirectory.dir("bin/test-resources"))
    }

    named("allTests") {
        dependsOn("copyTestResources")
    }

    withType<Test> {
        dependsOn("copyTestResources")

        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
    }
}

publishing {
    publications {
        getByName<MavenPublication>("kotlinMultiplatform") {
            val git = "LevelZ-File/cli"

            pom {
                name = "LevelZ CLI"
                description = project.description
                url = "https://levelz.calcugames.xyz"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/$git.git"
                    developerConnection = "scm:git:ssh://github.com/$git.git"
                    url = "https://github.com/$git"
                }
            }
        }
    }

    repositories {
        maven {
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }

            val releases = "https://repo.calcugames.xyz/repository/maven-releases/"
            val snapshots = "https://repo.calcugames.xyz/repository/maven-snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshots else releases)
        }
    }
}