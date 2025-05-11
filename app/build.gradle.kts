plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.taskapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.taskapp"
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
}

dependencies {
    // Existing dependencies from your new project (likely aliased in libs.versions.toml)
    implementation(libs.appcompat)
    implementation(libs.material)
    // implementation(libs.activity) // Often 'androidx.activity:activity-ktx' or similar, check your libs.versions.toml
    // For Java, 'androidx.activity:activity:1.8.0' or similar is fine.
    // If libs.activity points to a KTX version, it's okay.
    implementation(libs.constraintlayout)

    // ViewModel & LiveData (Android Architecture Components)
    // Make sure you have aliases for these in your libs.versions.toml
    // If not, use direct strings like: "androidx.lifecycle:lifecycle-viewmodel:2.7.0"
    implementation(libs.androidx.lifecycle.viewmodel) // Example alias
    implementation(libs.androidx.lifecycle.livedata)   // Example alias
    // annotationProcessor(libs.androidx.lifecycle.compiler) // Example alias, if needed for specific annotations

    // RecyclerView
    // Make sure you have an alias for this in your libs.versions.toml
    // If not, use direct string like: "androidx.recyclerview:recyclerview:1.3.2"
    implementation(libs.androidx.recyclerview) // Example alias

    // Firebase
    // Make sure you have aliases for these in your libs.versions.toml
    // If not, use direct strings like: platform("com.google.firebase:firebase-bom:32.7.4")
    implementation(platform(libs.firebase.bom))    // Example alias for Firebase BOM
    implementation(libs.firebase.firestore)        // Example alias for Firestore
    // implementation(libs.firebase.auth)          // Example alias, if you add authentication later

    // Testing - these look fine if they are correctly aliased
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit) // Typically 'androidx.test.ext:junit'
    androidTestImplementation(libs.espresso.core) // Typically 'androidx.test.espresso:espresso-core'
}