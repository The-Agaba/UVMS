plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.uvms"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.uvms"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.play.services.auth)

    // Core AndroidX
    implementation(libs.core)
    implementation(libs.appcompat.v161)

// Material Design Components
    implementation(libs.material.v1110)

// ConstraintLayout
    implementation(libs.constraintlayout.v214)

// ViewPager2 for slideshow
    implementation(libs.viewpager2)

// Vector Drawable Support
    implementation(libs.vectordrawable)

// Lifecycle (optional, useful for fragments in Java)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)

// Navigation (optional, if using fragments in Java)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)



    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}