import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Versions.KOTLIN_SERIALIZATION
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.dokka") version Versions.DOKKA_PLUGIN
    id("signing")
}

tasks {
    // Generate a Version.kt file with a constant for the version name
    register("generateVersionKt") {
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

    // Generate Javadoc (Dokka) Jar
    register<Jar>("dokkaHtmlJar") {
        archiveClassifier.set("javadoc")
        from("$buildDir/dokka")
        dependsOn(dokkaHtml)
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
                implementation("io.ktor", "ktor-client-core", Versions.KTOR)
                implementation("io.ktor", "ktor-client-json", Versions.KTOR)
                implementation("io.ktor", "ktor-client-serialization", Versions.KTOR)
                implementation("io.ktor", "ktor-client-logging", Versions.KTOR)
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
                implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", Versions.COROUTINES)
                implementation("io.ktor", "ktor-client-okhttp", Versions.KTOR)
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
                implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-android", Versions.COROUTINES)
                implementation("io.ktor", "ktor-client-okhttp", Versions.KTOR)
                implementation("org.slf4j", "slf4j-android", Versions.SLF4J)
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
//                implementation("io.ktor", "ktor-client-json-js", "$versions.ktor")
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }

        val macosMain by getting {
            dependencies {
                implementation("io.ktor", "ktor-client-curl", Versions.KTOR)
            }
        }
        val macosTest by getting {
        }

        val iosMain by getting {
            dependencies {
                implementation("io.ktor", "ktor-client-ios", Versions.KTOR)
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
}

// This must be done in `afterEvaluate` because otherwise it doesn't work for the Android artifact ¯\_(ツ)_/¯
afterEvaluate {
    publishing {
        repositories {
            maven {
                // Note: declare your user name / password in your home's gradle.properties like this:
                // mavenCentralNexusUsername = <user name>
                // mavenCentralNexusPassword = <password>
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                name = "mavenCentralNexus"
                credentials(PasswordCredentials::class)
            }
        }

        publications.withType<MavenPublication>().forEach { publication ->

            publication.artifact(tasks.getByName("dokkaHtmlJar"))

            publication.pom {
                name.set("klibqonto")
                description.set("A Qonto API client library for Kotlin, Java and more.")
                url.set("https://github.com/BoD/klibqonto")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("BoD")
                        name.set("Benoit 'BoD' Lubek")
                        email.set("BoD@JRAF.org")
                        url.set("https://JRAF.org")
                        organization.set("JRAF.org")
                        organizationUrl.set("https://JRAF.org")
                        roles.set(listOf("developer"))
                        timezone.set("+1")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/BoD/klibqonto")
                    developerConnection.set("scm:git:https://github.com/BoD/klibqonto")
                    url.set("https://github.com/BoD/klibqonto")
                }
                issueManagement {
                    url.set("https://github.com/BoD/klibqonto/issues")
                    system.set("GitHub Issues")
                }
            }
        }
    }

    signing {
        // Note: declare the signature key, password and file in your home's gradle.properties like this:
        // signing.keyId=<8 character key>
        // signing.password=<your password>
        // signing.secretKeyRingFile=<absolute path to the gpg private key>
        sign(publishing.publications)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// Run `./gradlew publishToMavenLocal` to publish to the local maven repo
// Run `./gradlew publish` to publish to Maven Central (then go to https://oss.sonatype.org/#stagingRepositories and "close", and "release")
