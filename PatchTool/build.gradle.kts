plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    jvmToolchain(11)
}

dependencies {
    api("com.wpf.utils.aliyun:patchtool:2.2.6")
    api("net.dongliu:apk-parser:2.6.10")
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")
}

afterEvaluate {
    publishing {
        publications {
            register("jitPack", MavenPublication::class.java) {
                from(components["java"])
                groupId = "com.wpf.utils"
                artifactId = "patchtool"
                version = "1.0.0"
            }
        }
    }
}
