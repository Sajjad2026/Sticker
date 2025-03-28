pluginManagement {
    repositories {
        maven ("https://developer.huawei.com/repo/")
        maven("https://jitpack.io")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://jitpack.io")
        google()
        mavenCentral()
        jcenter()
        maven ("https://artifacts.applovin.com/android" )
      //  maven ("https://developer.huawei.com/repo/")
    }
}

rootProject.name = "Whatsapp StickerMaker"
include(":app")
 