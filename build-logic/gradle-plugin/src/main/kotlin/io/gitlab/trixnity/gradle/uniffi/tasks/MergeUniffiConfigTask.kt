/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.gitlab.trixnity.gradle.uniffi.tasks

import io.gitlab.trixnity.gradle.uniffi.Config
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.peanuuutz.tomlkt.Toml
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class MergeUniffiConfigTask : DefaultTask() {
    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val originalConfig: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val kotlinVersion: Property<String>

    @get:Input
    @get:Optional
    abstract val useKotlinXSerialization: Property<Boolean>

    @get:OutputFile
    abstract val outputConfig: RegularFileProperty

    @TaskAction
    fun mergeConfig() {
        val originalConfigFile = originalConfig.orNull?.asFile
        val originalConfig = originalConfigFile?.let {
            toml.decodeFromString<Config>(it.readText(Charsets.UTF_8))
        } ?: Config()
        val result = originalConfig.copy(
            kotlinTargetVersion = originalConfig.kotlinTargetVersion
                ?: kotlinVersion.orNull?.takeIf { it.isNotBlank() },
            generateSerializableTypes = originalConfig.generateSerializableTypes
                ?: useKotlinXSerialization.orNull,
        )
        outputConfig.get().asFile.writeText(toml.encodeToString(result), Charsets.UTF_8)
    }

    companion object {
        private val toml = Toml {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
}