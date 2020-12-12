plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Versions.KOTLIN_SERIALIZATION
    id("com.android.library")
    id("maven-publish")
}

// Generate a Version.kt file with a constant for the version name
tasks.register("generateVersionKt") {
    val outputDir = layout.buildDirectory.dir("generated/source/kotlin").get().asFile
    outputs.dir(outputDir)
    doFirst {
        val outputWithPackageDir = File(outputDir, "org/jraf/klibqonto/internal/client").apply { mkdirs() }
        File(outputWithPackageDir, "Version.kt").writeText(
            """
                package org.jraf.klibqonto.internal.client
                internal const val VERSION = "${project.version}"
            """.trimIndent()
        )
    }
}

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
                languageVersion = "1.4"
            }
        }
    }

    android {
        publishLibraryVariants("release")
    }

    // Javascript is commented for now - I don't know how this actually works and how to test it
//    js {
//        browser {
//        }
//        nodejs {
//        }
//    }

    macosX64("macos") {
        binaries {
            framework()
        }
    }

    iosX64("ios") {
        binaries {
            framework()
        }
    }

    sourceSets.all {
        languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(tasks.getByName("generateVersionKt").outputs.files)

            dependencies {
                implementation(kotlin("stdlib"))
                implementation(dependency("io.ktor", "ktor-client-core", Versions.KTOR))
                implementation(dependency("io.ktor", "ktor-client-json", Versions.KTOR))
                implementation(dependency("io.ktor", "ktor-client-serialization", Versions.KTOR))
                implementation(dependency("io.ktor", "ktor-client-logging", Versions.KTOR))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(dependency("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", Versions.COROUTINES))
                implementation(dependency("io.ktor", "ktor-client-okhttp", Versions.KTOR))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(dependency("org.jetbrains.kotlinx", "kotlinx-coroutines-android", Versions.COROUTINES))
                implementation(dependency("io.ktor", "ktor-client-okhttp", Versions.KTOR))
                implementation(dependency("org.slf4j", "slf4j-android", Versions.SLF4J))
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        // Javascript is commented for now - I don't know how this actually works and how to test it
//        val jsMain by getting {
//            dependencies {
//                implementation(dependency("io.ktor", "ktor-client-json-js", "$versions.ktor"))
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }

        val macosMain by getting {
            dependencies {
                implementation(dependency("io.ktor", "ktor-client-curl", Versions.KTOR))
            }
        }
        val macosTest by getting {
        }

        val iosMain by getting {
            dependencies {
                implementation(dependency("io.ktor", "ktor-client-ios", Versions.KTOR))
            }
        }
        val iosTest by getting {
        }
    }
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    sourceSets {
        getByName("main").apply {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }

        getByName("androidTest").apply {
            java.srcDirs("src/androidTest/kotlin")
            res.srcDirs("src/androidTest/res")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

//    extensions.configure<KotlinJvmOptions>("kotlinOptions") {
//        jvmTarget = JavaVersion.VERSION_1_8.toString()
//    }
//    extensions.configure<KotlinJvmOptions>("kotlin") {
//        jvmTarget = JavaVersion.VERSION_1_8.toString()
//    }
}
