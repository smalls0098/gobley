/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.gobley.gradle.cargo.dsl

import dev.gobley.gradle.cargo.rust.targets.RustDesktopTarget

interface CargoDesktopBuildVariant<out RustTargetT : RustDesktopTarget> : CargoBuildVariant<RustTargetT> {
    override val build: CargoDesktopBuild<CargoDesktopBuildVariant<RustTargetT>>
}
