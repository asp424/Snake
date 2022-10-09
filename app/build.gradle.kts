plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

val composeVersion = "1.2.0-beta01"

android {
    compileSdk = 33
    defaultConfig {
        applicationId = appId
        minSdk = 24
        targetSdk = 33
        versionCode = 5
        versionName = appVersion
        testInstrumentationRunner = testRunner
        vectorDrawables { useSupportLibrary = true }
        buildTypes {
            debug {
                isMinifyEnabled = true
                isShrinkResources = true
            }
            release {
                isMinifyEnabled = true
                isShrinkResources = true
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
    implementation("com.google.firebase:firebase-messaging-ktx:23.0.8")
    implementation(project(mapOf("path" to ":chatFirebase")))

    //Base
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0-alpha02")
    implementation("androidx.activity:activity-compose:1.6.0")

    //Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.compiler:compiler:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.material3:material3:1.0.0-beta03")
    implementation("androidx.compose.ui:ui-tooling-preview:1.2.1")
    implementation("io.coil-kt:coil-compose:2.1.0")
}





