plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") //habilita las anotaciones para room
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.proyecto"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.proyecto"
        minSdk = 23
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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.room.runtime) //habolita el manejon de db local
    implementation(libs.androidx.room.ktx) //habilita las coroutines
    kapt(libs.androidx.room.compiler)  //genere el codigo de base de datos
    implementation(libs.kotlinx.coroutines.core)// tareas en backgroud
    implementation(libs.kotlinx.coroutines.android) //tareas asincronas
    implementation(libs.firebase.auth)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")


    implementation (libs.firebase.common)
    implementation(libs.firebase.firestore)
    implementation(platform(libs.firebase.bom))
}