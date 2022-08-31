plugins {
    id("org.jlleitschuh.gradle.ktlint").version("10.3.0")
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
        classpath("com.google.gms:google-services:4.3.13")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.1")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
