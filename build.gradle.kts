// SPDX-License-Identifier: MIT OR Apache-2.0

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget.*

plugins {
//  kotlin("multiplatform") version "1.6.20-RC-169"
//  kotlin("multiplatform") version "1.7.0-dev-1132"
    kotlin("multiplatform") version "1.7.0-dev-1920"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
}

kotlin {
    linuxArm64()
    linuxArm32Hfp()

    val commonMain by sourceSets.getting
    val nativeMain by sourceSets.creating {
        dependsOn(commonMain)
    }
    targets.withType<KotlinNativeTarget> {
        sourceSets["${targetName}Main"].apply {
            dependsOn(nativeMain)
        }
        compilations["main"].apply {
            cinterops.create("libui") {
                includeDirs("$rootDir")
            }
        }
    }
}
