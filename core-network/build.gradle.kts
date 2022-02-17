plugins {
    id("android-library-convention")
    id("kotlin-kapt")
    // "libs.versions" is not accessible in "plugins": https://youtrack.jetbrains.com/issue/KTIJ-19369
    id("com.apollographql.apollo3").version("3.0.0")
}

apollo {
    packageName.set("com.automotivecodelab.corenetwork")
    generateApolloMetadata.set(true)
    // alwaysGenerateTypesMatching.set(listOf())
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.apollo)
    implementation(libs.dagger)
    kapt(libs.daggerKapt)
}
