plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.protobuf")
}

android {
    namespace = "com.yuruneji.camera_training2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yuruneji.camera_training2"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt
    val hilt_version = "2.49"
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-android-compiler:$hilt_version")

    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // CameraX
    val camerax_version = "1.4.0-beta02"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:face-detection:16.1.5")

    // NanoHttpd
    implementation("org.nanohttpd:nanohttpd-webserver:2.3.1")

    // Retrofit
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")

    // Moshi
    val moshi_version = "1.14.0"
    implementation("com.squareup.moshi:moshi-kotlin:$moshi_version")

    // Coil
    val coil_version = "2.4.0"
    implementation("io.coil-kt:coil-compose:$coil_version")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Security-Crypto
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Apache Commons Lang 3.12.0
    implementation("org.apache.commons:commons-lang3:3.12.0")
    // Apache Commons Codec 1.15
    implementation("commons-codec:commons-codec:1.15")
    // Apache Commons Net 3.11.1
    implementation("commons-net:commons-net:3.11.1")

    // Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // LeakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")

    // datastore
    implementation("androidx.datastore:datastore:1.0.0")

    // protobuf
    implementation("com.google.protobuf:protobuf-javalite:3.22.4")

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.4"
    }
    // plugins {
    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
    // }
}
