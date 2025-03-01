/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package dev.gobley.uniffi.examples.app

import dev.gobley.uniffi.examples.app.windows.Window
import dev.gobley.uniffi.examples.todolist.TodoList

@OptIn(ExperimentalStdlibApi::class)
fun main() = ContentWindow(TodoList()).use { window ->
    window.open()
    Window.runMessageLoop()
}
