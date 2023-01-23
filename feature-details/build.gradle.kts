// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress(
    "DSL_SCOPE_VIOLATION"
)
plugins {
    id("android-library-convention")
    id("kotlin-kapt")
    alias(libs.plugins.apollo)
}

apollo {
    packageName.set("com.automotivecodelab.featuredetails")
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    namespace = "com.automotivecodelab.featuredetails"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(projects.coreUi)
    implementation(projects.common)
    implementation(projects.featureFavoritesApi)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeToolingPreview)
    implementation(libs.viewmodelCompose)
    implementation(libs.composeMaterial3)
    implementation(libs.coil)
    implementation(libs.bundles.retrofit)
    implementation(libs.apollo)
    apolloMetadata(projects.coreNetwork)
    implementation(projects.coreNetwork)
    implementation(libs.activityCompose)
    implementation(libs.accompanistPlaceholder)
    implementation(libs.accompanistPermissions)
    implementation(libs.timber)
    implementation(libs.datastore)
    // ===dagger===
    implementation(libs.dagger)
    kapt(libs.daggerKapt)
}
