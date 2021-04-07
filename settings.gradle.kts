pluginManagement {
    val kotlinVersion: String by settings
    val dokkaPluginVersion: String by settings
    val detektPluginVersion: String by settings
    val versionsPluginVersion: String by settings
    val publishPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaPluginVersion
        id("io.gitlab.arturbosch.detekt") version detektPluginVersion
        id("com.github.ben-manes.versions") version versionsPluginVersion
        id("io.github.gradle-nexus.publish-plugin") version publishPluginVersion
    }
}

rootProject.name = "ktypes"
