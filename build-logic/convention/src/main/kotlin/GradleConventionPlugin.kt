import org.gradle.api.Plugin
import org.gradle.api.Project
import com.softartdev.notedelight.configureSwiftPmOfflineResolution

class GradleConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configureSwiftPmOfflineResolution()
    }
}
