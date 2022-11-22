package it.unibo.gradle.plugin.greetings

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register

/**
 * Sample plugin.
 */
open class GreetingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("greetings", GreetingsExtension::class.java, target)
            tasks.register<GreetingTask>("greet") {
                greeting.set(extension.target)
            }
        }
    }
}

/**
 * Sample [project] extension.
 */
open class GreetingsExtension(project: Project) {

    internal val target: Property<String> = project.objects.property<String>()
        .apply { convention("everybody") }

    /**
     * Uses the string as a greeting.
     */
    fun String.asGreeting() = target.set(this)
}

/**
 * Greeting task.
 */
open class GreetingTask : DefaultTask() {

    init {
        group = "Custom tasks"
        description = "a task that just prints"
    }

    /**
     * The greeting.
     */
    @Input
    val greeting: Property<String> = project.objects.property()

    /**
     * The greeting message.
     */
    @Internal
    val message: Provider<String> = greeting.map { "I salute you with: $it" }

    /**
     * Actual task action. Prints a greeting.
     */
    @TaskAction
    fun printGreeting() {
        project.logger.warn(message.get())
    }
}
