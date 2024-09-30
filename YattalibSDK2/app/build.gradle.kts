plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "projeto.lib.yattalibsdk2"
    compileSdk = 33 // Adjust to your compileSdkVersion

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        // Note: Consumer ProGuard rules should be applied using a different approach in Kotlin DSL
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2") // Ensure JUnit is added here
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // For Android instrumentation tests
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // For UI tests
}

repositories {
    google()
    mavenCentral()
}