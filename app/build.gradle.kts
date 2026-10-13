plugins {
    // Начиная с AGP 9 поддержка Kotlin встроена в плагин com.android.application,
    // поэтому отдельный org.jetbrains.kotlin.android применять НЕ нужно —
    // AGP сам выдаёт ошибку, если его подключить.
    alias(libs.plugins.android.application)
    // Плагин компилятора Compose (для Kotlin 2.x он обязателен).
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.gasfinder"
    // Compose 1.12 (BOM 2026.09.00) требует собирать проект против API 37+
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.gasfinder"
        // Значение из методички: API 24 ("Nougat"; Android 7.0) — охватывает ~96% устройств
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Геолокация (методичка: play-services-location:21.0.1)
    implementation(libs.play.services.location)
    // Карта (методичка: osmdroid-android:6.1.18)
    implementation(libs.osmdroid.android)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
