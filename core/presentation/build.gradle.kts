plugins {
    alias(libs.plugins.convention.compose.multiplatform.library)
}

kotlin {
    applyDefaultHierarchyTemplate()
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                
                implementation(projects.core.domain)
                
                implementation(compose.components.resources)
                implementation(libs.material3.adaptive)
                implementation(libs.bundles.koin.common)
            }
        }
        
        val mobileMain by creating {
            dependencies {
                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.compose)
                implementation(libs.moko.permissions.notifications)
            }
            dependsOn(commonMain.get())
        }
        androidMain.get().dependsOn(mobileMain)
        iosMain.get().dependsOn(mobileMain)
    }
    
}