/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gobley.gradle.cargo.dsl

import gobley.gradle.Variant
import gobley.gradle.cargo.rust.targets.RustPosixTarget
import gobley.gradle.cargo.tasks.FindDynamicLibrariesTask
import gobley.gradle.utils.register
import org.gradle.api.Project
import javax.inject.Inject

@Suppress("LeakingThis")
abstract class CargoPosixBuildVariant @Inject constructor(
    project: Project,
    build: CargoPosixBuild,
    variant: Variant,
    extension: CargoExtension,
) : DefaultCargoBuildVariant<RustPosixTarget, CargoPosixBuild>(project, build, variant, extension),
    CargoJvmBuildVariant<RustPosixTarget>,
    CargoNativeBuildVariant<RustPosixTarget> {
    init {
        dynamicLibraries.addAll(build.dynamicLibraries)
        dynamicLibrarySearchPaths.addAll(build.dynamicLibrarySearchPaths)
        embedRustLibrary.convention(build.embedRustLibrary)
        resourcePrefix.convention(build.resourcePrefix)
        androidUnitTest.convention(build.androidUnitTest)
    }

    override val findDynamicLibrariesTaskProvider = project.tasks.register<FindDynamicLibrariesTask>({
        +this@CargoPosixBuildVariant
    }) {
        rustTarget.set(this@CargoPosixBuildVariant.rustTarget)
        libraryNames.set(this@CargoPosixBuildVariant.dynamicLibraries)
        searchPaths.set(this@CargoPosixBuildVariant.dynamicLibrarySearchPaths)
    }
}
