import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("android-application-convention")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    // "libs.versions" is not accessible in "plugins": https://youtrack.jetbrains.com/issue/KTIJ-19369
    id("com.apollographql.apollo3").version("3.0.0")
    id("com.google.firebase.crashlytics")
}

apollo {
    packageName.set("com.automotivecodelab.rtclient")
}

android {
    defaultConfig {
        applicationId = "com.automotivecodelab.rtclient"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField(
            "String", "SERVER_URL",
            gradleLocalProperties(rootDir).getProperty("SERVER_URL")
        )
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }
    // offline release build
//    buildTypes {
//        release {
//            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
//                mappingFileUploadEnabled = false
//            }
//        }
//    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    namespace = "com.automotivecodelab.rtclient"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.timber)
    implementation(libs.datastore)
    implementation(libs.paging)
    implementation(libs.bundles.retrofit)
    implementation(libs.apollo)
    apolloMetadata(projects.coreNetwork)
    implementation(libs.coil)
    debugImplementation(libs.leakCanary)

    // ===modules===
    implementation(projects.featureRssFeeds)
    implementation(projects.featureSearch)
    implementation(projects.coreUi)
    implementation(projects.coreNetwork)
    implementation(projects.featureDetails)
    implementation(projects.common)
    implementation(projects.featureFavoritesApi)
    implementation(projects.featureFavoritesImpl)

    // ===compose===
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeToolingPreview)
    implementation(libs.activityCompose)
    debugImplementation(libs.composeTooling)
    implementation(libs.navigationCompose)
    implementation(libs.viewmodelCompose)
    implementation(libs.pagingCompose)
    implementation(libs.composeMaterial3)
    implementation(libs.accompanistSystemUiController)
    implementation(libs.accompanistInsets)
    implementation(libs.accompanistNavigationAnimation)
    implementation(libs.accompanistNavigation)

    // ===test===
    testImplementation(libs.junit)
    androidTestImplementation(libs.junitExt)
    androidTestImplementation(libs.composeTest)
    testImplementation(libs.coroutinesTest)

    // ===firebase===
    // firebase takes care of versioning by itself using bom
    implementation(platform("com.google.firebase:firebase-bom:29.0.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // ===room===
    implementation(libs.room)
    kapt(libs.roomKapt)
    implementation(libs.roomKtx)

    // ===dagger===
    implementation(libs.dagger)
    kapt(libs.daggerKapt)
}
