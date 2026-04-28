// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}

val compose_version = "1.5.4"
val kotlin_version = "1.9.20"
val room_version = "2.6.1"
val hilt_version = "2.48.1"
val camerax_version = "1.3.1"
val mlkit_barcode_version = "17.2.0"
val coil_version = "2.5.0"
val vico_version = "1.13.1"