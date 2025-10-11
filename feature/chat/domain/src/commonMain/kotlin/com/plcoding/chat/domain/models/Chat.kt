package com.plcoding.chat.domain.models

import kotlin.time.Instant

data class Chat(
    val id: String,
    val participants: List<ChatParticipants>,
    val lastActivityAt: Instant,
    val lastMessage: String?,
)
