plugins {
    id("com.android.application")
}

android {
    namespace = "com.fritzvohn.airnudge"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fritzvohn.airnudge"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        val keystorePath = System.getenv("AIRNUDGE_KEYSTORE")
        val keystorePassword = System.getenv("AIRNUDGE_KEYSTORE_PASSWORD")
        val keyAliasValue = System.getenv("AIRNUDGE_KEY_ALIAS")
        val keyPasswordValue = System.getenv("AIRNUDGE_KEY_PASSWORD")
        if (!keystorePath.isNullOrBlank()
            && !keystorePassword.isNullOrBlank()
            && !keyAliasValue.isNullOrBlank()
            && !keyPasswordValue.isNullOrBlank()) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                keyAlias = keyAliasValue
                keyPassword = keyPasswordValue
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
