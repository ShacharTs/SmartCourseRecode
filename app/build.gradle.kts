plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.dagger.hilt.android")
    kotlin("kapt")

    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"

    id("com.google.gms.google-services")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("C:\\Users\\shach\\testjks")
            storePassword = "123456"
            keyAlias = "testalias"
            keyPassword = "123456"
        }

        buildTypes {
            getByName("release") {
                // ADD THIS LINE
                signingConfig = signingConfigs.getByName("release")

                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
    namespace = "com.smartcourse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.smartcourse"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }


    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            // Fixes the "6 files found with path 'META-INF/LICENSE.md'" error
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.generativeai)
    debugImplementation(libs.compose.ui.tooling)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation")
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation)



    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.4.0")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.4.0")
    implementation("io.github.jan-tennert.supabase:compose-auth:2.4.0")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.4.0")
    implementation("io.github.jan-tennert.supabase:supabase-kt:2.4.0")
    implementation("io.ktor:ktor-http:2.3.11")

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.1")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.android.gms:play-services-location:21.1.0")

    implementation("io.ktor:ktor-client-okhttp:2.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("androidx.compose.material:material-icons-extended:1.7.0")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    implementation("androidx.datastore:datastore-preferences:1.1.1")


    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

    // UI Testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.9")
    androidTestImplementation("io.mockk:mockk-agent:1.13.9")



    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.0")


}
