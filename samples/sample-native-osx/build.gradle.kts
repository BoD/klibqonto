plugins {
    kotlin("multiplatform")
}

kotlin {
    macosX64("macos") {
        binaries {
            executable()
        }
    }

    sourceSets {
        val macosMain by getting {
            dependencies {
                // Library
                implementation(project(":klibqonto"))
            }
        }
    }
}

// ./gradlew :sample-native-osx:build