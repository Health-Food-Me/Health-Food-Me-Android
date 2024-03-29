import org.jetbrains.kotlin.konan.properties.Properties

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    defaultConfig {
        applicationId = "org.helfoome"
        versionCode = 3
        versionName = "1.0.1"
        buildConfigField("String", "NAVER_CLIENT_ID", properties.getProperty("NAVER_CLIENT_ID"))
        buildConfigField("String", "NAVER_CLIENT_SECRET", properties.getProperty("NAVER_CLIENT_SECRET"))
        buildConfigField("String", "NAVER_CLIENT_NAME", properties.getProperty("NAVER_CLIENT_NAME"))
        buildConfigField("String", "KAKAO_APP_KEY", properties.getProperty("KAKAO_APP_KEY"))
        buildConfigField("String", "KAKAO_NATIVE_KEY", properties.getProperty("KAKAO_NATIVE_KEY"))
        buildConfigField("String", "NAVER_API_MAP_KEY", properties.getProperty("NAVER_API_MAP_KEY"))
        buildConfigField("String", "HFM_BASE_URL", properties.getProperty("HFM_BASE_URL"))
    }

    buildTypes {
        getByName("release") {
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
    debugImplementation(libs.leakCanary)
    implementation(libs.lottie)
    implementation(libs.kotlin.serialization.converter)
    implementation(libs.junit)
    implementation(libs.kakaologin)
    implementation(libs.naverlogin)
    implementation(libs.navermap)
    implementation(libs.googlegms)
    implementation(libs.flexbox)
    implementation(libs.gson)
    implementation(libs.image.picker)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.heydeal)
    implementation(libs.firebase.crashlyticsKtx)
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
