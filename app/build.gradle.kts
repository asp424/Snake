plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

val composeVersion = "1.2.0-beta01"

android {
    compileSdk = 32
    defaultConfig {
        applicationId = appId
        minSdk = 24
        targetSdk = 32
        versionCode = 1
        versionName = appVersion
        testInstrumentationRunner = testRunner
        vectorDrawables { useSupportLibrary = true }
        buildTypes {
            release {
                isMinifyEnabled = true
                proguardFiles(getDefaultProguardFile(proGName), proGRules)
            }
        }
        composeOptions { kotlinCompilerExtensionVersion = composeVersion }
        compileOptions { sourceCompatibility = javaVersion; targetCompatibility = javaVersion }
        kotlinOptions {
            jvmTarget = jvm; freeCompilerArgs = argsList
        }
        buildFeatures { compose = true }
        packagingOptions { resources { excludes += res } }
        namespace = "com.lm.snake"
    }
}

dependencies {

    //Dagger
    implementation("com.google.dagger:dagger:2.42")
    kapt("com.google.dagger:dagger-compiler:2.42")

    //Base
    implementation("androidx.core:core-ktx:1.9.0-alpha03")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0-rc01")
    implementation("androidx.activity:activity-compose:1.6.0-alpha03")

    //Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.compiler:compiler:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.material3:material3:1.0.0-alpha11")
    implementation("io.coil-kt:coil-compose:2.0.0-rc02")
}





