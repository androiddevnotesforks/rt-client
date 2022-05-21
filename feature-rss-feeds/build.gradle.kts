plugins {
    id("android-library-convention")
    id("kotlin-kapt")
    // "libs.versions" is not accessible in "plugins": https://youtrack.jetbrains.com/issue/KTIJ-19369
    id("com.apollographql.apollo3").version("3.0.0")
}

apollo {
    packageName.set("com.automotivecodelab.featurerssfeeds")
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    namespace = "com.automotivecodelab.featurerssfeeds"
}

dependencies {

    implementation(libs.bundles.android)
    implementation(projects.coreUi)
    implementation(projects.common)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeToolingPreview)
    implementation(libs.viewmodelCompose)
    implementation(libs.composeMaterial3)
    implementation(libs.bundles.retrofit)
    implementation(libs.timber)
    implementation(libs.apollo)
    apolloMetadata(projects.coreNetwork)
    implementation(projects.coreNetwork)
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
