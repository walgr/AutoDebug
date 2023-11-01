pluginManagement {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/nexus/content/repositories/releases")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://packages.aliyun.com/maven/repository/2428546-release-87ayOu") {
            credentials {
                username = "653b02ee970dc802e532f004"
                password = "ZwOcLGu7St6N"
            }
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/nexus/content/repositories/releases")
        maven("https://packages.aliyun.com/maven/repository/2428546-release-87ayOu") {
            credentials {
                username = "653b02ee970dc802e532f004"
                password = "ZwOcLGu7St6N"
            }
        }
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "AutoDebug"
include(":app")
include(":autodebug")
include(":fileserver")
include(":DexFix")
include(":PatchTool")
include("PatchTask")
