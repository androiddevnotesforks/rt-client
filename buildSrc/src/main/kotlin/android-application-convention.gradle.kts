import com.android.build.gradle.BaseExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("base-android-convention")
}

configure<BaseExtension> {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
}