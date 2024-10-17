plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("com.google.dagger.hilt.android") version "2.49" apply false
    // id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}
