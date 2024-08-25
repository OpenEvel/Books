import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

fun getLastVersion(defaultVersion: String = "1.0.0-SNAPSHOT"): String {
    val stdout = ByteArrayOutputStream()
    return try {
        exec {
            commandLine = "git describe --tags --abbrev=0".split(" ")
            standardOutput = stdout
        }
        val tag = stdout.toString().trim()
        if (tag.isEmpty()) {
            defaultVersion
        } else {
            // Удалить первую букву 'v', если она есть
            tag.removePrefix("v")
        }
    } catch (e: Exception) {
        defaultVersion
    }
}

tasks.register("lastVer") {
    doLast {
        val ver = getLastVersion()
        println("project verson: $ver")
        // Логика использования тега
    }
}

android {
    namespace = "ru.oraora.books"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.oraora.books"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = getLastVersion()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.ogson)

    // Coil
    implementation(libs.coil.compose)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.material)
    implementation(libs.reorderable)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.test.manifest)
}