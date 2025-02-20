plugins {
    id("uniffi-tests")
}

uniffi {
    generateFromLibrary {
        namespace = "kmm_ext_types_custom"
    }
}
