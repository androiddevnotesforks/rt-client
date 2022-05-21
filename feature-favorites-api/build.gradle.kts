plugins {
    id("android-library-convention")
    id("kotlin-kapt")
}


dependencies {
    implementation(libs.bundles.android)
}
android {
    namespace = "com.automotivecodelab.featurefavoritesapi"
}
