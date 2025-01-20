
internal actual typealias UniffiRustCallStatus = CPointer<{{ ci.namespace() }}.cinterop.UniffiRustCallStatus>
internal actual var UniffiRustCallStatus.code: Byte
    get() = pointed.code
    set(value) { pointed.code = value }
internal actual var UniffiRustCallStatus.errorBuf: RustBufferByValue
    get() = pointed.errorBuf.readValue()
    set(value) { value.place(pointed.errorBuf.ptr) }

internal actual typealias UniffiRustCallStatusByValue = CValue<{{ ci.namespace() }}.cinterop.UniffiRustCallStatus>
fun UniffiRustCallStatusByValue(
    code: Byte,
    errorBuf: RustBufferByValue
): UniffiRustCallStatusByValue {
    return cValue<{{ ci.namespace() }}.cinterop.UniffiRustCallStatus> {
        this.code = code
        errorBuf.write(this.errorBuf.rawPtr)
    }
}
internal actual var UniffiRustCallStatusByValue.code: Byte
    get() = useContents { code }
    set(value) {
        useContents {
            code = value
        }
    }
internal actual var UniffiRustCallStatusByValue.errorBuf: RustBufferByValue
    get() = useContents { errorBuf.readValue() }
    set(value) {
        useContents { 
            value.write(errorBuf.rawPtr)
        }
    }

internal actual object UniffiRustCallStatusHelper
internal actual fun UniffiRustCallStatusHelper.allocValue(): UniffiRustCallStatusByValue
    = cValue<{{ ci.namespace() }}.cinterop.UniffiRustCallStatus>()
internal actual fun <U> UniffiRustCallStatusHelper.withReference(
    block: (UniffiRustCallStatus) -> U
): U {
    return memScoped {
        val status = alloc<{{ ci.namespace() }}.cinterop.UniffiRustCallStatus>()
        block(status.ptr)
    }
}
