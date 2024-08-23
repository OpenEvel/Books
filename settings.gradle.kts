import java.util.Properties
import java.io.File

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Загрузка свойств из файла
val localProperties = Properties().apply {
    val localFile = File(rootProject.projectDir, "local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/moprules/ogson")
//            credentials {
//                // Имя пользователя GitHub (обычно GITHUB_ACTOR в Actions)
//                username = localProperties["gpr.user"] as String? ?: System.getenv("GITHUB_ACTOR")
//                // Токен доступа (обычно GITHUB_TOKEN в Actions)
//                password = localProperties["gpr.token"] as String? ?: System.getenv("GITHUB_TOKEN")
//            }
//        }
    }
}

rootProject.name = "Books"
include(":app")
 