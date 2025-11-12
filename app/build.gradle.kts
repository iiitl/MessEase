plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.theayushyadav11.MessEase"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.theayushyadav11.MessEase"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resConfigs("en")
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/ASL2.0",
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md"
            )
        }
    }
}

dependencies {
    // AndroidX core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx.v160)
    implementation(libs.androidx.legacy.support.v4)

    // Lifecycle + ViewModel
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx.v270)
    implementation(libs.androidx.navigation.ui.ktx.v270)

    // Firebase (only required ones)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging.ktx)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Room Database
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Glide
    implementation(libs.glide)
    kapt("com.github.bumptech.glide:compiler:4.13.2")

    // JSON + Network
    implementation(libs.gson)
    implementation(libs.okhttp)

    // Lottie Animations
    implementation(libs.lottie)

    // Razorpay Checkout
    implementation(libs.checkout)

    // Others
    implementation(libs.play.services.auth)
    implementation(libs.pinview)

    implementation(libs.commons.io)
    implementation(libs.android.mail)
    implementation(libs.android.activation)
    implementation(libs.google.auth.library.oauth2.http)

    // ExoPlayer
    implementation(libs.androidx.media3.exoplayer)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
