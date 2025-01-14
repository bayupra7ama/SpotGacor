plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)


}

android {
    namespace = "com.bayupratama.spotgacor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bayupratama.spotgacor"
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.github.bumptech.glide:glide:4.12.0" )// Pastikan menggunakan versi terbaru
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.kotlin.stdlib)
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation(libs.androidx.viewpager2)
    implementation( libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx.v274)
    implementation(libs.androidx.navigation.ui.ktx.v274)
    implementation(libs.coil)
    implementation(libs.androidx.viewpager2.v100)

    implementation (libs.gson)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation (libs.compressor)



}