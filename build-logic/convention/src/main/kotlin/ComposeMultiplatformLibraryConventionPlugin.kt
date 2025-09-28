import com.plcoding.chirp.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeMultiplatformLibraryConventionPlugin: Plugin<Project> {
    
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.plcoding.convention.kmp.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
            }
            
            dependencies {
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.compose.ui").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.compose.foundation").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.compose.material3").get()
                )
                add(
                    "commonMainImplementation",
                    libs.findLibrary("jetbrains.compose.material.icons.core").get()
                )
            }
        }
    }
}