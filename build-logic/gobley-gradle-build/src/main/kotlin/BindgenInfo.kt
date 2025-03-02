/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gobley.gradle.build

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.peanuuutz.tomlkt.Toml
import java.io.File

data class BindgenInfo(
    val name: String,
    val version: String,
    val binaryName: String,
) {
    companion object {
        fun fromCargoManifest(file: File): BindgenInfo {
            val manifestString = file.readText(Charsets.UTF_8)
            val manifest = CargoManifest.toml.decodeFromString<CargoManifest>(manifestString)
            return BindgenInfo(
                name = manifest.`package`.name,
                version = manifest.`package`.version,
                binaryName = manifest.binaries[0].name,
            )
        }
    }
}

@Serializable
internal data class CargoManifest(
    @SerialName("package") val `package`: Package,
    @SerialName("bin") val binaries: List<BinaryTarget>,
) {
    @Serializable
    data class Package(
        val name: String,
        val version: String,
    )

    @Serializable
    data class BinaryTarget(
        val name: String
    )

    companion object {
        val toml = Toml {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
}
