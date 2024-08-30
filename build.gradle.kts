plugins {
    kotlin("multiplatform") version "2.0.20"
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.calcugames.xyz/repository/maven-public/")
}

dependencies {
    commonMainImplementation("com.github.ajalt.clikt:clikt:4.4.0")
    commonMainImplementation("xyz.calcugames:levelz-kt:0.2.4")
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
            executable {
                entryPoint("xyz.calcugames.levelz.cli")
            }
        }
    }
}