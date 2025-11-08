plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.android)           // keep if this module uses Hilt
    alias(libs.plugins.jetbrains.kotlin.kapt)  // kapt for Hilt + Room
}

android {
    namespace = "com.muzz.core.data"   // align with project naming
    compileSdk = 36                    // align with the rest of the project

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    // Desugaring is usually only needed in the app module; keep here only if you
    // actually use Java 8+ APIs in this library and want to propagate it.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "17" }
    kotlin { jvmToolchain(17) }
}

dependencies {
    implementation(project(":core-domain"))

    // Room
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    // (optional) for tests:
    testImplementation(libs.room.testing)

    // DI
    implementation(libs.javax.inject)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Desugaring (see note above)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // ✅ Unit test deps (moved from implementation → testImplementation)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.androidx.test.ext.junit.ktx)
}
