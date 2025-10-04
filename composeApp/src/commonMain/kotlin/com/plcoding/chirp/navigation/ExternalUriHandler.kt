package com.plcoding.chirp.navigation

object ExternalUriHandler {
    
    private var cached: String? = null
    var listener: ((uri: String) -> Unit)? = null
        set(value) {
            field = value
            value?.let { value ->
                cached?.let { cached ->
                    value.invoke(cached)
                }
                cached = null
            }
        }
    
    fun onNewUri(uri: String) {
        cached = uri
        listener?.let {
            it.invoke(uri)
            cached = null
        }
    }
}