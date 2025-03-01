import dev.gobley.gradle.RustHost
import dev.gobley.gradle.cargo.dsl.jvm
import dev.gobley.gradle.uniffi.tasks.InstallBindgenTask

plugins {
    id("host-jvm-native-tests")
    id("dev.gobley.cargo")
    id("dev.gobley.uniffi")
}

cargo {
    builds.jvm {
        embedRustLibrary.set(rustTarget == RustHost.current.rustTarget)
    }
}

uniffi {
    bindgenFromPath(rootProject.layout.projectDirectory.dir("bindgen"))
}

tasks.withType<InstallBindgenTask> {
    quiet = false
}
