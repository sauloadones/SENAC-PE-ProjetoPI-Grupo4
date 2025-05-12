plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt") // ou alias se configurado no toml
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.example.appsenkaspi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.appsenkaspi"
        minSdk = 34
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX e Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // ViewModel e Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.security:security-crypto:1.1.0-alpha03")

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
