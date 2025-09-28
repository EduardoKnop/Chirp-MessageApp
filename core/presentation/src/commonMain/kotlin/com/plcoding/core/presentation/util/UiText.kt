package com.plcoding.core.presentation.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

// Only use in the Screens, Don't use in the ViewModels!
sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    class Resource(
        val id: StringResource,
        val args: Array<Any> = arrayOf(),
    ) : UiText
    
    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is Resource -> stringResource(resource = id, *args)
        }
    }
    
    suspend fun asStringAsync(): String {
        return when(this) {
            is DynamicString -> value
            is Resource -> getString(resource = id, *args)
        }
    }
}