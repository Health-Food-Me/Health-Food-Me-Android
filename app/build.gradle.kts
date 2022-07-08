plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    defaultConfig {
        applicationId = "org.helfoome"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    namespace = "org.helfoome"
}

dependencies {
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.dagger)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.coil.core)
    implementation(libs.okhttp.bom)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.timber)
    implementation(libs.leakCanary)
    implementation(libs.lottie)
    implementation(libs.kotlin.serialization.converter)
    implementation(libs.junit)
    debugImplementation(libs.bundles.flipper)
    debugImplementation(libs.flipper.network)
    kapt(libs.bundles.compiler)
}

ktlint {
    android.set(true)
    coloredOutput.set(true)
    verbose.set(true)
    outputToConsole.set(true)
    disabledRules.set(setOf("max-line-length", "no-wildcard-imports", "import-ordering"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}
