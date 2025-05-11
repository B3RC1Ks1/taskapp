// First plugins block - must be at the top for applying base plugins like 'com.android.application'
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services) // Apply the plugin here using its alias
}

android {
    namespace = "com.example.taskapp"
    // Keep your target, compile, min SDKs
    compileSdk = 35 // Using 35 as specified, make sure it's available

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
        sourceCompatibility = JavaVersion.VERSION_11 // Keep your Java versions
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Note: If you use Kotlin, you'd add a kotlinOptions block here
}

dependencies {
    // Existing dependencies from your new project (likely aliased in libs.versions.toml)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity) // Ensure this points to the non-KTX version if using Java primarily
    implementation(libs.constraintlayout)

    // ViewModel & LiveData (Android Architecture Components)
    implementation(libs.androidx.lifecycle.viewmodel) // Ensure this points to the non-KTX version
    implementation(libs.androidx.lifecycle.livedata)   // Ensure this points to the non-KTX version
    // annotationProcessor(libs.androidx.lifecycle.compiler) // If you use specific lifecycle annotations requiring it

    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // Firebase
    // !!! IMPORTANT !!!
    // Ensure you have downloaded the 'google-services.json' file from your Firebase project
    // and placed it in the 'app' module directory (usually 'app/google-services.json')
    // ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    // implementation(libs.firebase.auth)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}