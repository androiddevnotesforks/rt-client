@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


configure<BaseExtension> {

    setCompileSdkVersion(31)

    defaultConfig {
        minSdk = 21
        targetSdk = 31
        versionCode = 4
        versionName = "1.0.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    // ktlint
    // kotlin packages shows as java packages if enabled
//    sourceSets {
//        val kotlinAdditionalSourceSets = project.file("src/main/kotlin")
//        findByName("main")?.java?.srcDirs(kotlinAdditionalSourceSets)
//    }
}
