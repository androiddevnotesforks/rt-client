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
    generateApolloMetadata.set(true)
    packageName.set("com.automotivecodelab.corenetwork")
    alwaysGenerateTypesMatching.set(listOf("Query"))
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.apollo)
    implementation(libs.dagger)
    kapt(libs.daggerKapt)
}
android {
    namespace = "com.automotivecodelab.corenetwork"
}
