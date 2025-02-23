/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.gitlab.trixnity.gradle.cargo.dsl

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.io.File

interface HasDynamicLibraries {
    /**
     * The set of directories containing the dynamic libraries required in runtime. This property is effective only for
     * Android and JVM targets.
     *
     * The plugin will automatically configure NDK library paths considering the API level. For example, the following
     * DSL will copy `libc++_shared.so` and `<API Level>/libaaudio.so` from the NDK directory to the app, without
     * manually modifying this property.
     * ```kotlin
     * cargo {
     *   builds.android {
     *     dynamicLibraries = arrayOf("aaudio", "c++_shared")
     *   }
     * }
     * ```
     *
     * The Cargo build output directory (e.g., `target/<triplet>/{debug, release}`) will also be included here.
     */
    val dynamicLibrarySearchPaths: SetProperty<File>

    /**
     * The names of dynamic libraries required in runtime without the prefix and the file extension. This property is
     * effective only for Android and JVM targets.
     *
     * The following DSL will copy `libfoo.so` from `/path/to/libraries`.
     * ```kotlin
     * cargo {
     *   builds.jvm {
     *     dynamicLibrarySearchPaths.add(File("/path/to/libraries"))
     *     dynamicLibraries.add("foo")
     *   }
     * }
     * ```
     */
    val dynamicLibraries: SetProperty<String>

    /**
     * Defaults to `true`.
     *
     * When `true`, the Rust shared library is built using Cargo and embedded into the Kotlin build result.
     * For JVM targets, the shared library will included in the resulting `.jar` file. For Android targets,
     * the shared library will be included in the resulting `.aab`, `.aar`, or `.apk` file.
     *
     * Set this to `false` when you implement your own build logic to load the shared library, another
     * crate using UniFFI is referencing this crate (See the UniFFI external types documentation), or you
     * just don't want to make your application/library target for that platform.
     *
     * When the host does not support building for this target, this property is ignored and considered `false`.
     * When NDK ABI filters in the `android {}` block are configured to ignore this target, this property
     * is ignored as well.
     *
     * [embedRustLibrary] in [CargoBuild] is set to the convention value of [embedRustLibrary] in
     * [CargoBuildVariant]. The value in [CargoBuildVariant] is used.
     *
     * Even when [embedRustLibrary] is false, if the [CargoBuild] is chosen to be used to build UniFFI bindings,
     * Cargo build will be invoked.
     */
    val embedRustLibrary: Property<Boolean>
}
