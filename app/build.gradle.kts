plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.ocrtest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ocrtest"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Text to speech
    implementation("androidx.core:core-ktx:1.12.0")

    // 🧠 ML Kit Text Recognition (On-device)
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // Thêm retrofit để gọi API
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //OkHttp
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Navbar
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel hỗ trợ trực tiếp cho Jetpack Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Compose Foundation Layout (padding, Column, Row, Box...)
    implementation(libs.androidx.foundation.layout.android)

    // AppCompat (giúp tương thích ngược với các version Android cũ, cung cấp theme & compat widget)
    implementation(libs.androidx.appcompat)

    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)

    val roomVersion = "2.7.0-alpha07"

    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}