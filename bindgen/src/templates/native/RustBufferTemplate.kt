{% include "ffi/RustBufferTemplate.kt" %}

internal typealias RustBuffer = CPointer<{{ ci.namespace() }}.cinterop.RustBuffer>

internal var RustBuffer.capacity: Long
    get() = pointed.capacity
    set(value) { pointed.capacity = value }
internal var RustBuffer.len: Long
    get() = pointed.len
    set(value) { pointed.len = value }
internal var RustBuffer.data: Pointer?
    get() = pointed.data
    set(value) { pointed.data = value?.reinterpret() }
internal fun RustBuffer.asByteBuffer(): ByteBuffer? {
    val buffer = ByteBuffer()
    val data = pointed.data?.reinterpret<kotlinx.cinterop.ByteVar>() ?: return null

    if (pointed.len < 0L)
        throw IllegalStateException("Trying to call asByteBuffer with negative length")

    if (pointed.len == 0L)
        return buffer

    // Copy over bytes 1 by 1
    for (i in 0..len - 1) {
        buffer.put(data[i])
    }
    
    return buffer
}

internal typealias RustBufferByValue = CValue<{{ ci.namespace() }}.cinterop.RustBuffer>
fun RustBufferByValue(
    capacity: Long,
    len: Long,
    data: Pointer?,
): RustBufferByValue {
    return cValue<{{ ci.namespace() }}.cinterop.RustBuffer> {
        this.capacity = capacity
        this.len = len
        this.data = data?.reinterpret()
    }
}
internal val RustBufferByValue.capacity: Long
    get() = useContents { capacity }
internal val RustBufferByValue.len: Long
    get() = useContents { len }
internal val RustBufferByValue.data: Pointer?
    get() = useContents { data }
internal fun RustBufferByValue.asByteBuffer(): ByteBuffer? {
    val buffer = ByteBuffer()
    val data = useContents { data }?.reinterpret<kotlinx.cinterop.ByteVar>() ?: return null
    val len = useContents { len }
    if (len < 0L)
        throw IllegalStateException("Trying to call asByteBuffer with negative length")

    if (len == 0L)
        return buffer

    // Copy over bytes 1 by 1
    for (i in 0..<len) {
        buffer.put(data[i])
    }
    
    return buffer   
}

internal object RustBufferHelper
internal fun RustBufferHelper.allocFromByteBuffer(buffer: ByteBuffer): RustBufferByValue
     = uniffiRustCall() { status ->
        // Note: need to convert the size to a `Long` value to make this work with JVM.
        UniffiLib.INSTANCE.{{ ci.ffi_rustbuffer_alloc().name() }}(buffer.limit().toLong(), status)!!
    }.also {
        val size = buffer.limit()
        it.useContents {
            val notNullData = data
            checkNotNull(notNullData) { "RustBuffer.alloc() returned null data pointer (size=${size})" }

           for (i in 0..<size) {
                notNullData[i.toInt()] = buffer.get().toUByte()
           }

        }
    }

/**
 * The equivalent of the `*mut RustBuffer` type.
 * Required for callbacks taking in an out pointer.
 *
 * Size is the sum of all values in the struct.
 */
internal typealias RustBufferByReference = CPointer<{{ ci.namespace() }}.cinterop.RustBufferByReference>

internal fun RustBufferByReference.setValue(value: RustBufferByValue) {
    pointed.capacity = value.capacity
    pointed.len = value.len
    pointed.data = value.data?.reinterpret()
}
internal fun RustBufferByReference.getValue(): RustBufferByValue
    = pointed.reinterpret<{{ ci.namespace() }}.cinterop.RustBuffer>().readValue()


internal typealias ForeignBytes = CPointer<{{ ci.namespace() }}.cinterop.ForeignBytes>
internal var ForeignBytes.len: Int
    get() = pointed.len
    set(value) { pointed.len = value }
internal var ForeignBytes.data: Pointer?
    get() = pointed.data
    set(value) { pointed.data = value?.reinterpret() }

internal typealias ForeignBytesByValue = CValue<{{ ci.namespace() }}.cinterop.ForeignBytes>
internal val ForeignBytesByValue.len: Int
    get() = useContents { len }
internal val ForeignBytesByValue.data: Pointer?
    get() = useContents { data }
