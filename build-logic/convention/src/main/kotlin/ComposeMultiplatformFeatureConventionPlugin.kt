import com.plcoding.chirp.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeMultiplatformFeatureConventionPlugin : Plugin<Project> {
    
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.plcoding.convention.multiplatform.library.compose")
            }
            
            dependencies {
                add(
                    "commonMainImplementation",
                    project(":core:presentation")
                )
                add(
                    "commonMainImplementation",
                    project(":core:designsystem")
                )
                
                add(
                    "commonMainImplementation",
                    platform(libs.findLibrary("koin.bom").get())
                )
                add(
                    "androidMainImplementation",
                    platform(libs.findLibrary("koin.bom").get())
                )
                
                add(
                    "commonMainImplementation",
                    libs.findLibrary("koin.compose").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("koin.compose.viewmodel").get()
                )
                
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.compose.runtime").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.compose.viewmodel").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.lifecycle.viewmodel").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.lifecycle.compose").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.lifecycle.viewmodel.savedstate").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.savedstate").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.bundle").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.compose.navigation").get()
                )
                
                add(
                    "androidMainImplementation",
                    libs.findLibrary("koin.android").get()
                )
                add(
                    "androidMainImplementation",
                    libs.findLibrary("koin.androidx.compose").get()
                )
                add(
                    "androidMainImplementation",
                    libs.findLibrary("koin.androidx.navigation").get()
                )
                add(
                    "androidMainImplementation",
                    libs.findLibrary("koin.core.viewmodel").get()
                )
            }
        }
    }
}