plugins {
    id("org.jlleitschuh.gradle.ktlint").version("10.2.1")
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        disabledRules.add("no-wildcard-imports")
        android.set(true)
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    // firebase
    dependencies {
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

// workaround for build failure with multi-module apollo config
tasks.all {
    if (name == "checkServiceApolloDuplicates") {
        enabled = false
    }
}
