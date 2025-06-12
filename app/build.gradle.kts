plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
}

android {
    buildFeatures {
        viewBinding = true
    }
    
    namespace = "com.example.securenotes"
    compileSdk = 35
    
    defaultConfig {
        applicationId = "com.example.securenotes"
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
    
    kotlinOptions {
        jvmTarget = "11"
    }
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintlayout)
    
    // Lifecycle components
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    
    // Navigation Component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // RecyclerView
    implementation(libs.recyclerview)
    
    // CoordinatorLayout
    implementation(libs.androidx.coordinatorlayout)
    
    // SwipeRefreshLayout
    implementation(libs.swiperefreshlayout)
    
    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    
    // Coroutines
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${libs.versions.coroutines.get()}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
    // Hilt testing
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)
}
