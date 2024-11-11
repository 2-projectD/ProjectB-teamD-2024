pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.google.com")
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        }
    }
}

//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//        // Chaquopyリポジトリの追加
//        maven { url =uri("https://chaquo.com/maven") }
//    }
//    dependencies {
//        classpath ("com.android.tools.build:gradle:7.2.0") // 適切なバージョンを使用)
//        classpath ("com.chaquo.python:gradle:12.0.0") // Chaquopyのプラグインを追加)
//    }
//}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://chaquo.com/maven") }
        maven { url = uri("https://maven.google.com") }
    }
}

rootProject.name = "ProjectB-D2024"
include(":app")
