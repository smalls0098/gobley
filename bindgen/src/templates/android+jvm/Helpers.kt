
@Structure.FieldOrder("code", "errorBuf")
internal open class UniffiRustCallStatusStruct(
    code: Byte,
    errorBuf: RustBufferByValue,
) : Structure() {
    @JvmField internal var code: Byte = code
    @JvmField internal var errorBuf: RustBufferByValue = errorBuf

    constructor(): this(0.toByte(), RustBufferByValue())

    internal class ByValue(
        code: Byte,
        errorBuf: RustBufferByValue,
    ): UniffiRustCallStatusStruct(code, errorBuf), Structure.ByValue {
        constructor(): this(0.toByte(), RustBufferByValue())
    }
    internal class ByReference(
        code: Byte,
        errorBuf: RustBufferByValue,
    ): UniffiRustCallStatusStruct(code, errorBuf), Structure.ByReference {
        constructor(): this(0.toByte(), RustBufferByValue())
    }
}

internal actual typealias UniffiRustCallStatus = UniffiRustCallStatusStruct.ByReference
internal actual var UniffiRustCallStatus.code: Byte
    get() = this.code
    set(value) { this.code = value }
internal actual var UniffiRustCallStatus.errorBuf: RustBufferByValue
    get() = this.errorBuf
    set(value) { this.errorBuf = value }

internal actual typealias UniffiRustCallStatusByValue = UniffiRustCallStatusStruct.ByValue
internal actual var UniffiRustCallStatusByValue.code: Byte
    get() = this.code
    set(value) { this.code = value }
internal actual var UniffiRustCallStatusByValue.errorBuf: RustBufferByValue
    get() = this.errorBuf
    set(value) { this.errorBuf = value }

internal actual object UniffiRustCallStatusHelper
internal actual fun UniffiRustCallStatusHelper.allocValue(): UniffiRustCallStatusByValue
    = UniffiRustCallStatusByValue()
internal actual fun <U> UniffiRustCallStatusHelper.withReference(
    block: (UniffiRustCallStatus) -> U
): U {
    val status = UniffiRustCallStatus()
    return block(status)
}
