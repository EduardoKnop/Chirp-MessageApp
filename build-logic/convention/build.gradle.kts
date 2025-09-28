import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.plcoding.convention.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.plcoding.convention.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidComposeApplication") {
            id = "com.plcoding.convention.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("composeMultiplatformApplication") {
            id = "com.plcoding.convention.multiplatform.application.compose"
            implementationClass = "ComposeMultiplatformApplicationConventionPlugin"
        }
        register("kmpLibrary") {
            id = "com.plcoding.convention.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("composeMultiplatformLibrary") {
            id = "com.plcoding.convention.multiplatform.library.compose"
            implementationClass = "ComposeMultiplatformLibraryConventionPlugin"
        }
        register("composeMultiplatformFeature") {
            id = "com.plcoding.convention.multiplatform.feature.compose"
            implementationClass = "ComposeMultiplatformFeatureConventionPlugin"
        }
    }
}