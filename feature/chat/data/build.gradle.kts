import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.buildkonfig)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                
                implementation(projects.core.domain)
                implementation(projects.core.data)
                
                implementation(projects.feature.chat.domain)
                implementation(projects.feature.chat.database)
                implementation(libs.bundles.ktor.common)
                implementation(libs.koin.core)
            }
        }
        
        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.androidx.lifecycle.process)
            }
        }
        
        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }
    
    targets.withType<KotlinNativeTarget> {
        compilations.getByName("main") {
            cinterops {
                create("network") {
                    defFile(file("src/nativeInterop/cinterop/network.def"))
                }
            }
        }
    }
}