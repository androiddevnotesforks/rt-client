plugins {
    id("android-library-convention")
    id("kotlin-kapt")
}


android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    namespace = "com.automotivecodelab.featurefavoritesimpl"
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
    implementation(libs.bundles.retrofit)
    implementation(libs.timber)
    implementation(libs.activityCompose)
    implementation(libs.junit)
    implementation(libs.accompanistInsets)

    // ===dagger===
    implementation(libs.dagger)
    kapt(libs.daggerKapt)

    // ===room===
    implementation(libs.room)
    kapt(libs.roomKapt)
    implementation(libs.roomKtx)
}