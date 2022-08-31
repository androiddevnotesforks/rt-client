plugins {
    id("android-library-convention")
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    namespace = "com.automotivecodelab.coreui"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeToolingPreview)
    implementation(libs.viewmodelCompose)
    // importing NoInternetConnectionException
    implementation(projects.coreNetwork)
    implementation(libs.accompanistSystemUiController)
}
