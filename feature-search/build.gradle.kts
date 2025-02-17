// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress(
    "DSL_SCOPE_VIOLATION"
)
plugins {
    id("android-library-convention")
    id("kotlin-kapt")
    alias(libs.plugins.apollo)
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    namespace = "com.automotivecodelab.featuresearch"
}

apollo {
    packageName.set("com.automotivecodelab.featuresearch")
}

dependencies {

    implementation(libs.bundles.android)
    implementation(projects.coreUi)
    implementation(projects.common)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeToolingPreview)
    implementation(libs.viewmodelCompose)
    implementation(libs.bundles.retrofit)
    implementation(libs.apollo)
    apolloMetadata(projects.coreNetwork)
    implementation(projects.coreNetwork)
    implementation(projects.featureFavoritesApi)
    implementation(libs.paging)
    implementation(libs.pagingCompose)
    implementation(libs.timber)
    implementation(libs.activityCompose)
    implementation(libs.accompanistSystemUiController)

    // ===dagger===
    implementation(libs.dagger)
    kapt(libs.daggerKapt)
}
