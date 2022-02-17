@file:Suppress("UnstableApiUsage")


enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
    }
}
rootProject.name = "rt-client"
include(
    "app",
    "feature-rss-feeds",
    "core-ui",
    "core-network",
    "feature-details-bottom-sheet",
    "feature-search",
    "common"
)
