package com.plcoding.chat.domain.models

data class MessageWithSender(
    val message: ChatMessage,
    val sender: ChatParticipants,
    val deliveryStatus: ChatMessageDeliveryStatus?,
)
