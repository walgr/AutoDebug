plugins {
    id("kotlin")
    id("java-gradle-plugin")
    id("maven-publish")
}

val versionName = "1.0.0"

gradlePlugin {
    plugins {
        register("PatchPlugin") {
            id = "pers.wpf.plugins.patchplugin"
            version = versionName
            implementationClass = "pers.wpf.plugins.PatchPlugin"
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            register("jitPack", MavenPublication::class.java) {
                from(components["java"])
                groupId = "pers.wpf.plugins.patchplugin"
                artifactId = "patchtask"
                version = versionName
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    jvmToolchain(11)
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("com.android.tools.build:gradle:7.4.2")

    api("com.wpf.utils:patchtool:1.0.0")
    implementation("com.google.guava:guava:18.0")
}