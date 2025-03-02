/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gobley.gradle.cargo

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.ProcessJavaResTask
import com.android.build.gradle.tasks.MergeSourceSetFolders
import gobley.gradle.Variant
import gobley.gradle.getByVariant
import gobley.gradle.variant
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import java.io.File

internal interface CargoPluginAndroidDelegate {
    val androidSdkRoot: File
    val androidMinSdk: Int
    val androidNdkRoot: File?
    val androidNdkVersion: String?
    val abiFilters: Set<String>

    fun addTestSourceDir(variant: Variant, resourceDirectory: Provider<Directory>)
    fun addMainJniDir(
        project: Project,
        variant: Variant,
        jniTask: TaskProvider<*>,
        jniDirectory: Provider<Directory>,
    )

    fun addUnitTestResource(
        project: Project,
        variant: Variant,
        resourceTask: TaskProvider<*>,
        resourceDirectory: Provider<List<File>>,
    )
}

internal fun CargoPluginAndroidDelegate(project: Project): CargoPluginAndroidDelegate {
    return CargoPluginAndroidDelegateImpl(project)
}

private class CargoPluginAndroidDelegateImpl(project: Project) :
    CargoPluginAndroidDelegate {
    private val androidExtension: BaseExtension = project.extensions.getByType()

    override val androidSdkRoot: File
        get() = androidExtension.sdkDirectory

    // TODO: Read <uses-sdk> from AndroidManifest.xml
    // androidExtension.sourceSets.getByName("main").manifest.srcFile
    override val androidMinSdk: Int
        get() = androidExtension.defaultConfig.minSdk ?: 21
    override val androidNdkRoot: File?
        get() = androidExtension.ndkPath?.let(::File)
    override val androidNdkVersion: String?
        get() = androidExtension.ndkVersion.takeIf(String::isNotEmpty)
    override val abiFilters: Set<String>
        get() = androidExtension.defaultConfig.ndk.abiFilters

    override fun addTestSourceDir(variant: Variant, resourceDirectory: Provider<Directory>) {
        androidExtension.sourceSets {
            val testSourceSet = getByVariant("test", variant)
            testSourceSet.resources.srcDir(resourceDirectory)
        }
    }

    override fun addMainJniDir(
        project: Project,
        variant: Variant,
        jniTask: TaskProvider<*>,
        jniDirectory: Provider<Directory>
    ) {
        project.tasks.withType<MergeSourceSetFolders> {
            if (name.lowercase().contains("jni")) {
                if (variant == this.variant!!) {
                    inputs.dir(jniDirectory)
                    dependsOn(jniTask)
                }
            }
        }

        androidExtension.sourceSets {
            val mainSourceSet = getByVariant(variant)
            mainSourceSet.jniLibs.srcDir(jniDirectory)
        }
    }

    override fun addUnitTestResource(
        project: Project,
        variant: Variant,
        resourceTask: TaskProvider<*>,
        resourceDirectory: Provider<List<File>>
    ) {
        project.tasks.withType<ProcessJavaResTask>().configureEach {
            if (name.contains("UnitTest") && variant == this.variant!!) {
                dependsOn(resourceTask)
                // Override the default behavior of AGP excluding .so files, which causes UnsatisfiedLinkError
                // on Linux.
                from(
                    // Append a fileTree which only includes the Rust shared library.
                    resourceDirectory
                )
            }
        }
    }
}