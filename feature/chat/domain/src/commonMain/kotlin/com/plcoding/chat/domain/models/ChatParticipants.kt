package com.plcoding.chat.domain.models

data class ChatParticipants(
    val userId: String,
    val userName: String,
    val profilePictureUrl: String?,
) {
    val initials: String
        get() = userName.take(2).uppercase()
}
