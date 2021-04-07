import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmVersion: String by project
val detektFormattingVersion: String by project
val kotestVersion: String by project

extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt")
    id("com.github.ben-manes.versions")
    id("io.github.gradle-nexus.publish-plugin")
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("reflect"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektFormattingVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

kotlin {
    explicitApi()
}

java {
    withSourcesJar()
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(javadocJar.get())
            pom {
                name.set("KTypes")
                description.set("KTypes is a zero-dependency Kotlin library for accurately introspecting type information")
                url.set("https://github.com/pluses/ktypes")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("CloudPluses")
                        name.set("CloudPluses Team")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/pluses/ktypes.git")
                    developerConnection.set("scm:git:ssh://github.com/pluses/ktypes.git")
                    url.set("https://github.com/pluses/ktypes/")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            val sonatypeUsername = findProperty("sonatypeUsername")?.toString() ?: System.getenv("SONATYPE_USERNAME")
            val sonatypePassword = findProperty("sonatypePassword")?.toString() ?: System.getenv("SONATYPE_PASSWORD")

            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(sonatypeUsername)
            password.set(sonatypePassword)
        }
    }
}

signing {
    val signingKey = findProperty("signingKey")?.toString() ?: System.getenv("SIGNING_KEY")
    val signingPassword = findProperty("signingPassword")?.toString() ?: System.getenv("SIGNING_PASSWORD")

    setRequired({
        (extra["isReleaseVersion"] as Boolean) && gradle.taskGraph.hasTask("publish")
    })

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = jvmVersion
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    withType<Detekt> {
        jvmTarget = jvmVersion
    }

    test {
        useJUnitPlatform()
    }
}

configure<DetektExtension> {
    buildUponDefaultConfig = true
    config = rootProject.files("detekt.yml")
}
