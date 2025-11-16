plugins {
    // Usa SOLO los alias del Version Catalog (evita duplicados)
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services) // Google Services plugin para Firebase
}

android {
    namespace = "com.example.proyectoandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proyectoandroid"
        minSdk = 25
        targetSdk = 36
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

    // Si tu proyecto ya compila con 11, mantenlo; si usas AGP reciente puedes subir a 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:34.4.0")) // 🔹 actualizada
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // ---- MVVM Architecture Components ----
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.7") // Para ViewModelProvider

    // ---- Android UI / test (lo que ya tenías) ----
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    
    // RecyclerView para listas
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
