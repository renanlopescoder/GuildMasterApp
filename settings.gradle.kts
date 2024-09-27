pluginManagement {
    repositories {
        google()
        mavenCentral()
       // maven { url ;"https://repo1.maven.org/maven2" }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GuildMasterApp"
include(":app")
 