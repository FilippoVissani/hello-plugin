plugins {
    `java-gradle-plugin` // Injects the plugin classpath into the tests
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gitSemVer)
    id("com.gradle.plugin-publish") version "1.0.0"
    id("org.danilopianini.gradle-kotlin-qa") version "0.27.0"
    alias(libs.plugins.dokka)
    signing
    `maven-publish`
    id("com.dorongold.task-tree") version "2.1.0"
}

group = "io.github.filippovissani"

repositories {
    mavenCentral()
}

gitSemVer {
    buildMetadataSeparator.set("-")
    maxVersionLength.set(20)
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.kotlin.testing)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.test {
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    )
}
val testWithJVM18 by tasks.registering(Test::class) { // Also works with JavaExec
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(18))
        }
    )
} // You can pick JVM's not yet supported by Gradle!
tasks.check {
    dependsOn(testWithJVM18)
}

gradlePlugin {
    plugins {
        create("") { // One entry per plugin
            id = "${project.group}.${project.name}"
            displayName = "LSS Greeting plugin"
            description = "Example plugin for the LSS course"
            implementationClass = "it.unibo.gradle.plugin.greetings.GreetingPlugin"
        }
    }
}

pluginBundle { // These settings are set for the whole plugin bundle
    website = "https://filippovissani.github.io/"
    vcsUrl = "https://github.com/FilippoVissani/hello-plugin"
    tags = listOf("example", "greetings", "lss", "unibo")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
    }
}

val javadocJar by tasks.registering(Jar::class) {
    from(tasks.dokkaJavadoc.get().outputDirectory)
    archiveClassifier.set("javadoc")
}

val sourceJar by tasks.registering(Jar::class) {
    from(sourceSets.named("main").get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            // Pass the pwd via -PmavenCentralPwd='yourPassword'
            val mavenCentralPwd: String by project
            credentials {
                username = "filippovissani"
                password = mavenCentralPwd
            }
        }
        publications {
            val lssGreetings by creating(MavenPublication::class) {
                from(components["java"])
                // If the gradle-publish-plugins plugin is applied, these are pre-configured
                // artifact(javadocJar)
                // artifact(sourceJar)
                pom {
                    name.set("Greetings plugin")
                    description.set("A test plugin")
                    url.set("https://github.com/FilippoVissani/hello-plugin")
                    licenses {
                        license {
                            name.set("MIT")
                        }
                    }
                    developers {
                        developer {
                            name.set("Filippo Vissani")
                        }
                    }
                    scm {
                        url.set("git@github.com:FilippoVissani/hello-plugin.git")
                        connection.set("git@github.com:FilippoVissani/hello-plugin.git")
                    }
                }
            }
            signing { sign(lssGreetings) }
        }
    }
}
