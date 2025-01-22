actual val uniffiRustFutureContinuationCallbackCallback: Any = object: UniffiRustFutureContinuationCallback {
    override fun callback(handle: Long, pollResult: Byte) {
        uniffiContinuationHandleMap.remove(handle).resume(pollResult)
    }
}

{%- if ci.has_async_callback_interface_definition() %}

actual val uniffiForeignFutureFreeImpl: Any = object: UniffiForeignFutureFree {
    override fun callback(handle: Long) {
        val job = uniffiForeignFutureHandleMap.remove(handle)
        if (!job.isCompleted) {
            job.cancel()
        }
    }
}

{%- endif %}