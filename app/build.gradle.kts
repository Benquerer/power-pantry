import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //glide
    id("kotlin-kapt")
}

android {
    namespace = "pt.ipt.dam.powerpantry"
    compileSdk = 35

    defaultConfig {
        applicationId = "pt.ipt.dam.powerpantry"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                load(file.inputStream())
            }
        }

        val sheetyApiKey = localProperties.getProperty("SHEETY_API_KEY") ?: "ERRER LOADING API KEY"

        buildConfigField("String", "SHEETY_API_KEY", "\"$sheetyApiKey\"")

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

    buildFeatures{
        dataBinding = true
        buildConfig = true
        viewBinding = true
    }

}


dependencies {

    // API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Barcode reading
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // Image Slider
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    // Swipe refresh
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //pass hashing
    implementation ("org.mindrot:jbcrypt:0.4")

    //Glide (image from url handling)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}