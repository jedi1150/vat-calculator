import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class UninstallConflictingPackagesTask : DefaultTask() {
    @get:Input
    abstract val adbPath: Property<String>

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun uninstall() {
        val adb = adbPath.get()
        listOf(
            "com.sandello.ndscalculator",
            "com.sandello.ndscalculator.baselineprofile",
        ).forEach { pkg ->
            execOperations.exec {
                commandLine(adb, "shell", "am", "force-stop", pkg)
                isIgnoreExitValue = true
            }
            execOperations.exec {
                commandLine(adb, "uninstall", pkg)
                isIgnoreExitValue = true
            }
            execOperations.exec {
                commandLine(adb, "shell", "pm", "uninstall", "--user", "0", pkg)
                isIgnoreExitValue = true
            }
        }
    }
}
