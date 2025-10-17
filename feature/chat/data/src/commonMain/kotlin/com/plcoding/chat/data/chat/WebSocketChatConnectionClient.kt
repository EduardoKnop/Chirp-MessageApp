package com.plcoding.chat.data.chat

import com.plcoding.chat.data.dto.websocket.IncomingWebSocketDto
import com.plcoding.chat.data.dto.websocket.IncomingWebSocketType
import com.plcoding.chat.data.dto.websocket.WebSocketMessageDto
import com.plcoding.chat.data.mappers.toDomain
import com.plcoding.chat.data.mappers.toEntity
import com.plcoding.chat.data.network.KtorWebSocketConnector
import com.plcoding.chat.database.ChirpChatDatabase
import com.plcoding.chat.domain.chat.ChatConnectionClient
import com.plcoding.chat.domain.chat.ChatRepository
import com.plcoding.core.domain.auth.SessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.json.Json

class WebSocketChatConnectionClient(
    webSocketConnector: KtorWebSocketConnector,
    private val chatRepository: ChatRepository,
    private val database: ChirpChatDatabase,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    applicationScope: CoroutineScope,
): ChatConnectionClient {
    
    override val chatMessages = webSocketConnector
        .messages
        .mapNotNull { parseIncomingMessage(it) }
        .onEach { handleIncomingMessage(it) }
        .filterIsInstance<IncomingWebSocketDto.NewMessageDto>()
        .mapNotNull {
            database.chatMessageDao.getMessageById(it.id)?.toDomain()
        }
        .shareIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5_000L),
        )
    
    override val connectionState = webSocketConnector.connectionState
    
    private fun parseIncomingMessage(message: WebSocketMessageDto): IncomingWebSocketDto? {
        return when (message.type) {
            IncomingWebSocketType.NEW_MESSAGE.name -> {
                json.decodeFromString<IncomingWebSocketDto.NewMessageDto>(message.payload)
            }
            IncomingWebSocketType.MESSAGE_DELETED.name -> {
                json.decodeFromString<IncomingWebSocketDto.MessageDeletedDto>(message.payload)
            }
            IncomingWebSocketType.PROFILE_PICTURE_UPDATED.name -> {
                json.decodeFromString<IncomingWebSocketDto.ProfilePictureUpdatedDto>(message.payload)
            }
            IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED.name -> {
                json.decodeFromString<IncomingWebSocketDto.ChatParticipantChangedDto>(message.payload)
            }
            else -> null
        }
    }
    
    private suspend fun handleIncomingMessage(message: IncomingWebSocketDto) {
        when (message) {
            is IncomingWebSocketDto.ChatParticipantChangedDto -> refreshChat(message)
            is IncomingWebSocketDto.MessageDeletedDto -> deleteMessage(message)
            is IncomingWebSocketDto.NewMessageDto -> handleNewMessage(message)
            is IncomingWebSocketDto.ProfilePictureUpdatedDto -> updateProfilePicture(message)
        }
    }
    
    private suspend fun refreshChat(message: IncomingWebSocketDto.ChatParticipantChangedDto) {
        chatRepository.fetchChatById(message.chatId)
    }
    
    private suspend fun deleteMessage(message: IncomingWebSocketDto.MessageDeletedDto) {
        database.chatMessageDao.deleteMessageById(message.messageId)
    }
    
    private suspend fun handleNewMessage(message: IncomingWebSocketDto.NewMessageDto) {
        val chatExists = database.chatDao.getChatById(message.chatId) != null
        if (!chatExists) {
            chatRepository.fetchChatById(message.chatId)
        }
        
        val entity = message.toEntity()
        database.chatMessageDao.upsertMessage(entity)
    }
    
    private suspend fun updateProfilePicture(message: IncomingWebSocketDto.ProfilePictureUpdatedDto) {
        database.chatParticipantDao.updateProfilePictureUrl(
            userId = message.userId,
            newUrl = message.newUrl,
        )
        
        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
        if (authInfo != null && authInfo.user.id == message.userId) {
            sessionStorage.set(
                info = authInfo.copy(
                    user = authInfo.user.copy(
                        profilePictureUrl = message.newUrl,
                    ),
                ),
            )
        }
    }
}