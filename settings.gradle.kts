@file:Suppress("UnstableApiUsage")


enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "rt-client"
include(
    "app",
    "feature-rss-feeds",
    "core-ui",
    "core-network",
    "feature-details",
    "feature-search",
    "common",
    "feature-favorites-api",
    "feature-favorites-impl",
)
