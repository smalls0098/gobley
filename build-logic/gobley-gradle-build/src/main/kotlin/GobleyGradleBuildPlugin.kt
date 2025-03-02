/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gobley.gradle.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class GobleyGradleBuildPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create<GobleyGradleBuildExtension>("gobleyGradleBuild")
    }
}