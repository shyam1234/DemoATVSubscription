plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.malviya.demosubscriptionandroidtv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.malviya.demosubscriptionandroidtv"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Billing
    implementation(libs.billing.ktx)
    implementation(libs.billing)
    //ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    //for viewmodel declaration
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //coin
    implementation (libs.koin.android  )      // Koin Android
    //implementation (libs.koin.androidx.viewmodel)  // Koin ViewModel support
    //flow
    implementation (libs.kotlinx.coroutines.core )    // Coroutines Core
    implementation (libs.kotlinx.coroutines.android) // Coroutines for Android (if you're working on Android)
    //Timber
    implementation (libs.timber)
}