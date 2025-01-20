actual val uniffiRustFutureContinuationCallbackCallback: Any = staticCFunction { handle: Long, pollResult: Byte ->
    uniffiContinuationHandleMap.remove(handle).resume(pollResult)
}

{%- if ci.has_async_callback_interface_definition() %}

actual val uniffiForeignFutureFreeImpl: Any = staticCFunction { handle: Long ->
    val job = uniffiForeignFutureHandleMap.remove(handle)
    if (!job.isCompleted) {
        job.cancel()
    }
}

{%- endif %}