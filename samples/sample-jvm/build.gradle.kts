import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("java")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        languageVersion = "1.4"
    }
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib-jdk8", Versions.KOTLIN))
    implementation(dependency("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", Versions.COROUTINES))

    // Library
    implementation(project(":klibqonto"))
}
