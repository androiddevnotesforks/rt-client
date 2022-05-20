plugins {
    id("android-library-convention")
    id("kotlin-kapt")
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