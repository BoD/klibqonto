object Versions {
    // Misc and plugins
    const val GRADLE = "6.7.1"
    const val KOTLIN = "1.4.21"
    const val BEN_MANES_VERSIONS_PLUGIN = "0.36.0"
    const val ANDROID_GRADLE_PLUGIN = "4.1.1"
    const val DOKKA_PLUGIN = "1.4.20"

    // Lib dependencies
    const val KOTLIN_SERIALIZATION = "1.4.21"
    const val KTOR = "1.4.3"
    const val COROUTINES = "1.4.2"
    const val SLF4J = "1.7.30"

    // Testing dependencies
    const val ESPRESSO = "3.3.0"
    const val JUNIT = "4.13.1"
}

object AppConfig {
    const val APPLICATION_ID = "org.jraf.android.fotomator"
    const val COMPILE_SDK = 30
    const val TARGET_SDK = 30
    const val MIN_SDK = 23

    var buildNumber: Int = 0
    val buildProperties = mutableMapOf<String, String>()
}