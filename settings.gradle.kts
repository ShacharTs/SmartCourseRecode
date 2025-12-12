pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }


    plugins {
        id("com.google.gms.google-services") version "4.4.1"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SmartCourse"
include(":app")
