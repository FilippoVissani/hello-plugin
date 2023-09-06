plugins {
    id("com.gradle.enterprise") version "3.14"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.0.23"
}

rootProject.name = "lss2022.helloplugin"

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

val destination = File(".git/hooks/commit-msg")

gitHooks {
    commitMsg {
        conventionalCommits()
    }
    createHooks(true)
}
