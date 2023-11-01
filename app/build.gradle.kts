import pers.wpf.plugins.PatchConfig

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("pers.wpf.plugins.patchplugin") version "1.0.0"
}

//if (!isPublish.toBoolean()) {
    apply(plugin = "pers.wpf.plugins.patchplugin")
//}

configure<PatchConfig> {
    serverBaseUrl = ""
    signFilePath = "../AutoDebugRelease.jks"
    signAlias = "quick"
    keyStorePassword = "walgr1010"
    keyPassword = "walgr1010"
}

android {
    namespace = "com.wpf.autodebug.demo"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.wpf.autodebug"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            this.abiFilters.add("armeabi")
            this.abiFilters.add("armeabi-v7a")
            this.abiFilters.add("arm64-v8a")
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../AutoDebugRelease.jks")
            storePassword = "walgr1010"
            keyAlias = "quick"
            keyPassword = "walgr1010"
            enableV1Signing = true
            enableV2Signing = true
        }
        create("release") {
            storeFile = file("../AutoDebugRelease.jks")
            storePassword = "walgr1010"
            keyAlias = "quick"
            keyPassword = "walgr1010"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    kotlin {
        jvmToolchain(11)
    }
    lint.checkReleaseBuilds = false
    lint.abortOnError = false
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
}