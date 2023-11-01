plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.wpf.autodebug"
    compileSdk = 33
    defaultConfig {
        minSdk = 19
//        applicationId = "com.wpf.autodebug"
//        targetSdk = 33
//        versionCode = 1
//        versionName = "1.0.0"
//        ndk {
//            this.abiFilters.add("armeabi")
//            this.abiFilters.add("armeabi-v7a")
//            this.abiFilters.add("arm64-v8a")
//        }
    }
//    signingConfigs {
//        getByName("debug") {
//            storeFile = file("../AutoDebugRelease.jks")
//            storePassword = "walgr1010"
//            keyAlias = "quick"
//            keyPassword = "walgr1010"
//            enableV1Signing = true
//            enableV2Signing = true
//        }
//        create("release") {
//            storeFile = file("../AutoDebugRelease.jks")
//            storePassword = "walgr1010"
//            keyAlias = "quick"
//            keyPassword = "walgr1010"
//            enableV1Signing = true
//            enableV2Signing = true
//        }
//    }
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//            signingConfig = signingConfigs.getByName("debug")
//        }
//    }
    kotlinOptions {
        jvmTarget = "11"
    }
    kotlin {
        jvmToolchain(11)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    lint.checkReleaseBuilds = false
    lint.abortOnError = false
}

dependencies {
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")
    implementation("com.aliyun.ams:alicloud-android-hotfix:3.3.8")
}