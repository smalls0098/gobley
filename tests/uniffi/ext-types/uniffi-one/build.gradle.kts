plugins {
    id("uniffi-tests")
}

uniffi {
    generateFromLibrary {
        namespace = "kmm_uniffi_one"
    }
}
