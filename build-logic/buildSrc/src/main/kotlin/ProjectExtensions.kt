/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.withType

fun Project.configureGobleyGradleProject(
    description: String
) {
    configureProjectProperties(description)

    tasks.withType<Test> {
        useJUnitPlatform()
        reports {
            it.junitXml.required.set(true)
        }
    }
    if (!plugins.hasPlugin("com.gradle.plugin-publish")) {
        createDefaultPublication()
    }

    configurePom()
    configureMavenCentralPublishing()
}

private fun Project.configureProjectProperties(
    description: String
) {
    val bindgenInfo = BindgenInfo.fromCargoManifest(
        rootProject.layout.projectDirectory.file("../bindgen/Cargo.toml").asFile
    )
    group = "dev.gobley.gradle"
    version = bindgenInfo.version
    this.description = description
}

private fun Project.createDefaultPublication() {
    extensions.configure(PublishingExtension::class.java) { publishing ->
        publishing.publications.create("default", MavenPublication::class.java) { publication ->
            publication.from(components.findByName("java"))
            publication.artifact(javaSourcesTask())
            publication.artifact(javadocTask())
        }
    }
}

private fun Project.javaSourcesTask(): TaskProvider<Jar> {
    return tasks.register("javaSourcesJar", Jar::class.java) { javaSourcesJar ->
        javaSourcesJar.archiveClassifier.set("sources")
        val sourceSets = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets
        javaSourcesJar.from(sourceSets.getByName("main").allSource)
    }
}

private fun Project.javadocTask(): TaskProvider<Jar> {
    return tasks.register("javadocJar", Jar::class.java) { javadocJar ->
        javadocJar.archiveClassifier.set("javadoc")
        javadocJar.from(
            resources.text.fromString("For documentation, see https://github.com/gobley/gobley")
        ) { resource ->
            resource.rename { "readme.txt" }
        }
    }
}

private fun Project.configurePom() {
    extensions.configure(PublishingExtension::class.java) { publishing ->
        val mavenPublications = publishing.publications.withType(MavenPublication::class.java)
        mavenPublications.configureEach { publication ->
            publication.groupId = project.group.toString()
            publication.artifactId = project.name
            publication.version = project.version.toString()
            publication.pom { pom ->
                pom.name.set(project.name)
                pom.description.set(project.description)
                pom.inceptionYear.set("2023")
                pom.url.set(propertyOrEnv("gobley.projects.gradle.pom.url"))
                pom.licenses { licenses ->
                    licenses.license { license ->
                        license.name.set(propertyOrEnv("gobley.projects.gradle.pom.license.name"))
                        license.url.set(propertyOrEnv("gobley.projects.gradle.pom.license.url"))
                    }
                }
                pom.developers { developers ->
                    for (developerIdx in generateSequence(0) { it + 1 }) {
                        val propertyNamePrefix = "gobley.projects.gradle.pom.developer$developerIdx"
                        val developerId = propertyOrEnv("$propertyNamePrefix.id") ?: break
                        val developerName = propertyOrEnv("$propertyNamePrefix.name") ?: break
                        developers.developer { developer ->
                            developer.id.set(developerId)
                            developer.name.set(developerName)
                        }
                    }
                }
                pom.scm { scm ->
                    scm.url.set(propertyOrEnv("gobley.projects.gradle.pom.scm.url"))
                    scm.connection.set(propertyOrEnv("gobley.projects.gradle.pom.scm.connection"))
                    scm.developerConnection.set(propertyOrEnv("gobley.projects.gradle.pom.scm.developerConnection"))
                }
            }
        }
    }
}

private fun Project.propertyOrEnv(propertyName: String, envName: String = propertyName): String? {
    return findProperty(propertyName)?.toString()
        ?: rootProject.findProperty(propertyName)?.toString()
        ?: System.getenv(envName)
}

private fun Project.configureMavenCentralPublishing() {
    extensions.configure(PublishingExtension::class.java) { publishing ->
         publishing.repositories.maven { repository ->
             repository.name = "staging"
             repository.url = uri(layout.buildDirectory.dir("staging-deploy").get())
         }
    }
}
