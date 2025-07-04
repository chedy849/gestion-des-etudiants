plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.gestetudiant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gestetudiant"
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
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Material Design


    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Lottie
    implementation ("com.airbnb.android:lottie:5.2.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    // SwipeRefreshLayout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // CardView
    implementation ("androidx.cardview:cardview:1.0.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}