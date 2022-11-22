import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File
import kotlin.io.path.createTempDirectory

class TestGreetingPlugin : StringSpec(
    {
        "the plugin should load" {
            val buildFile = """
                plugins {
                    id("io.github.filippovissani.lss2022.helloplugin")
                }
                
                greetings {
                    "pippo".asGreeting()
                }
            """.trimIndent()
            val directory: File = createTempDirectory().toFile()
            directory.mkdirs()
            with(File(directory, "build.gradle.kts")) {
                writeText(buildFile)
            }
            val result = GradleRunner.create()
                .withProjectDir(directory)
                .withPluginClasspath()
                .withArguments("greet")
                .build()
            println(result.output)
            result.tasks.forEach { it.outcome shouldNotBe TaskOutcome.FAILED }
        }
    }
)
