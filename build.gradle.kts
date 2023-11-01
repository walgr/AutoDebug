plugins {
    id("com.android.application") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.10" apply false
//    id("pers.wpf.plugins.patchplugin") version "1.0.0" apply false
}

buildscript {
    dependencies {
        classpath("pers.wpf.plugins.patchplugin:patchtask:1.0.0")
    }
}