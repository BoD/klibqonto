enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "klibqonto-root"

include(":library")
project(":library").name = "klibqonto"

// Include all the sample modules from the "samples" directory
file("samples").listFiles()!!.forEach { dir ->
    include(dir.name)
    project(":${dir.name}").projectDir = dir
}
