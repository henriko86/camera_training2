import com.android.build.api.dsl.VariantDimension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    // id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.yuruneji.camera_training"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yuruneji.camera_training2"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // buildConfigField("boolean", "FROM_PROPERTIES", "${project.properties["from_properties"]}")
        // buildConfigField("boolean", "FROM_PROPERTIES", "${project.properties["from_properties"]}")
        // buildConfigField("String", "HOGE_API_KEY", "\"${project.properties["hoge_api_key"]}\"")
        // buildConfigField("int[]", "HOGE_ARRAY", "{1, 2, 3}")
        // buildConfigField("String[]", "PROPERTIES_ARRAY", "${project.properties["hoge_array"]}")
        // buildConfigField("int", "HOGE_VERSION", "${project.properties["hoge_version"]}")
        // buildConfigField("double", "HOGE_PI", "${project.properties["hoge_pi"]}")
        // buildConfigField("java.util.ArrayList<String>", "HUGA_LIST", "new java.util.ArrayList<>()")

        bindProperty {
            stringConfigField("API_URL_BASE", "${project.properties["API_URL_BASE"]}")
            stringConfigField("API_URL_DEVELOP", "${project.properties["API_URL_DEVELOP"]}")
            stringConfigField("API_URL_STAGING", "${project.properties["API_URL_STAGING"]}")
            stringConfigField("API_URL_PRODUCTION", "${project.properties["API_URL_PRODUCTION"]}")

            // stringConfigField("STRING_CONFIG_FIELD", "string string")
            // intConfigField("INT_CONFIG_FIELD", 111)
            // longConfigField("LONG_CONFIG_FIELD", 222L)
            // floatConfigField("FLOAT_CONFIG_FIELD", 333.333f)
            // doubleConfigField("DOUBLE_CONFIG_FIELD", 44.44)
            // booleanConfigField("BOOLEAN_CONFIG_FIELD", default = true)
            // stringResource("STRING_RESOURCE", "xxx")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
        viewBinding = true
        dataBinding = true
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

    // DI: Hilt
    val hilt_version = "2.49"
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-android-compiler:$hilt_version")
    // ksp("com.google.dagger:hilt-compiler:$hilt_version")

    // DB: Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    // ksp("androidx.room:room-compiler:2.5.0")
    implementation("androidx.room:room-ktx:$room_version")

    // Camera: CameraX
    val camerax_version = "1.4.0-beta02"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    // mobile SDK: ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:face-detection:16.1.5")

    // WebServer: NanoHttpd
    implementation("org.nanohttpd:nanohttpd-webserver:2.3.1")

    // HTTP Client:Retrofit
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")

    // Json: Moshi
    val moshi_version = "1.14.0"
    implementation("com.squareup.moshi:moshi-kotlin:$moshi_version")

    // Image: Coil
    val coil_version = "2.4.0"
    implementation("io.coil-kt:coil-compose:$coil_version")

    // Log: Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Crypto: Security-Crypto
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

    // Debug: LeakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")

}

interface BindPropertyScope {
    fun stringConfigField(name: String, default: String = "")
    fun intConfigField(name: String, default: Int = 0)
    fun longConfigField(name: String, default: Long = 0L)
    fun floatConfigField(name: String, default: Float = 0f)
    fun doubleConfigField(name: String, default: Double = 0.0)
    fun booleanConfigField(name: String, default: Boolean = false)
    fun stringResource(name: String, default: String = "")
}

fun VariantDimension.bindProperty(configure: BindPropertyScope.() -> Unit) {
    object : BindPropertyScope {
        override fun stringConfigField(name: String, default: String) {
            val value = (project.properties[name] as? String) ?: default
            buildConfigField("String", name, "\"$value\"")
        }

        override fun intConfigField(name: String, default: Int) {
            val value = (project.properties[name] as? String)?.toInt() ?: default
            buildConfigField("int", name, value.toString())
        }

        override fun longConfigField(name: String, default: Long) {
            val value = (project.properties[name] as? String)?.toLong() ?: default
            buildConfigField("long", name, value.toString())
        }

        override fun floatConfigField(name: String, default: Float) {
            val value = (project.properties[name] as? String)?.toFloat() ?: default
            buildConfigField("float", name, value.toString())
        }

        override fun doubleConfigField(name: String, default: Double) {
            val value = (project.properties[name] as? String)?.toDouble() ?: default
            buildConfigField("double", name, value.toString())
        }

        override fun booleanConfigField(name: String, default: Boolean) {
            val value = (project.properties[name] as? String)?.toBoolean() ?: default
            buildConfigField("boolean", name, value.toString())
        }

        override fun stringResource(name: String, default: String) {
            val value = (project.properties[name] as? String) ?: default
            resValue("string", name, value)
        }
    }.configure()
}
