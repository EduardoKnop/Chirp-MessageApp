package com.plcoding.chat.presentation.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.chat.domain.models.ChatMessage
import com.plcoding.chat.domain.models.ChatMessageDeliveryStatus
import com.plcoding.chat.presentation.chat_detail.components.MessageBox
import com.plcoding.chat.presentation.chat_detail.components.MessageList
import com.plcoding.chat.presentation.chat_list.components.ChatDetailHeader
import com.plcoding.chat.presentation.components.ChatHeader
import com.plcoding.chat.presentation.model.ChatUi
import com.plcoding.chat.presentation.model.MessageUi
import com.plcoding.core.designsystem.components.avatar.ChatParticipantUi
import com.plcoding.core.designsystem.theme.ChirpTheme
import com.plcoding.core.designsystem.theme.extended
import com.plcoding.core.presentation.util.UiText
import com.plcoding.core.presentation.util.clearFocusOnTap
import com.plcoding.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random
import kotlin.time.Clock

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatDetailRoot(
    chatId: String?,
    isDetailPresent: Boolean,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }
    
    BackHandler(
        enabled = !isDetailPresent,
    ) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(null))
        onBack()
    }
    
    ChatDetailScreen(
        state = state,
        isDetailPresent = isDetailPresent,
        onAction = viewModel::onAction,
    )
}

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    isDetailPresent: Boolean,
    onAction: (ChatDetailAction) -> Unit,
) {
    val configuration = currentDeviceConfiguration()
    val messageListState = rememberLazyListState()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        contentColor = if (!configuration.isWideScreen) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.extended.surfaceLower
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .clearFocusOnTap()
                .padding(innerPadding)
                .then(
                    if (configuration.isWideScreen) {
                        Modifier.padding(horizontal = 8.dp)
                    } else Modifier
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DynamicRoundedCornerColumn(
                    isCornersRounded = configuration.isWideScreen,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    ChatHeader {
                        ChatDetailHeader(
                            chatUi = state.chatUi,
                            isDetailPresent = isDetailPresent,
                            isChatOptionsDropDownOpen = state.isChatOptionsOpen,
                            onChatOptionsClick = { onAction(ChatDetailAction.OnChatOptionsClick) },
                            onDismissChatOptions = { onAction(ChatDetailAction.OnDismissChatOptions) },
                            onManageChatClick = { onAction(ChatDetailAction.OnChatMembersClick) },
                            onLeaveChatClick = { onAction(ChatDetailAction.OnLeaveChatClick) },
                            onBackClick = { onAction(ChatDetailAction.OnBackClick) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    
                    MessageList(
                        messages = state.messages,
                        listState = messageListState,
                        onMessageLongClick = { message ->
                            onAction(ChatDetailAction.OnMessageLongClick(message))
                        },
                        onMessageRetryClick = { message ->
                            onAction(ChatDetailAction.OnRetryClick(message))
                        },
                        onDismissMessageMenu = { onAction(ChatDetailAction.OnDismissMessageMenu) },
                        onDeleteMessageClick = { message ->
                            onAction(ChatDetailAction.OnDeleteMessageClick(message))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                    
                    AnimatedVisibility(
                        visible = !configuration.isWideScreen && state.chatUi != null,
                    ) {
                        MessageBox(
                            messageTextFieldState = state.messageTextFieldState,
                            isTextInputEnabled = state.canSendMessage,
                            connectionState = state.connectionState,
                            onSendClick = { onAction(ChatDetailAction.OnSendMessageClick) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                
                if (configuration.isWideScreen) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                AnimatedVisibility(
                    visible = configuration.isWideScreen && state.chatUi != null,
                ) {
                    MessageBox(
                        messageTextFieldState = state.messageTextFieldState,
                        isTextInputEnabled = state.canSendMessage,
                        connectionState = state.connectionState,
                        onSendClick = { onAction(ChatDetailAction.OnSendMessageClick) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun DynamicRoundedCornerColumn(
    isCornersRounded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = if (isCornersRounded) 4.dp else 0.dp,
                shape = if (isCornersRounded) RoundedCornerShape(16.dp) else RectangleShape,
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = if (isCornersRounded) RoundedCornerShape(16.dp) else RectangleShape,
            )
    ) {
        content()
    }
}

@Preview
@Composable
private fun ChatDetailScreenEmptyPreview() {
    ChirpTheme {
        ChatDetailScreen(
            state = ChatDetailState(),
            isDetailPresent = true,
            onAction = { },
        )
    }
}

@Preview
@Composable
private fun ChatDetailScreenMessagesPreview() {
    ChirpTheme(darkTheme = true) {
        ChatDetailScreen(
            state = ChatDetailState(
                messageTextFieldState = rememberTextFieldState(
                    initialText = "This is a sample message",
                ),
                canSendMessage = true,
                chatUi = ChatUi(
                    id = "1",
                    localParticipant = ChatParticipantUi(
                        id = "1",
                        username = "John Doe",
                        initials = "JD",
                    ),
                    otherParticipants = listOf(
                        ChatParticipantUi(
                            id = "2",
                            username = "Jane Smith",
                            initials = "JS",
                        ),
                        ChatParticipantUi(
                            id = "3",
                            username = "Alice Johnson",
                            initials = "AJ",
                        ),
                    ),
                    lastMessage = ChatMessage(
                        id = "1",
                        chatId = "1",
                        senderId = "2",
                        content = "Hello, how are you? I need to tell you something important. " +
                                "Please get back to me when you can. Thanks! This is a long message to test the overflow handling in the UI component.",
                        createdAt = Clock.System.now(),
                    ),
                    lastMessageSenderUsername = "Jane Smith",
                ),
                messages = (1..20).map {
                    val showLocalMessage = Random.nextBoolean()
                    if (showLocalMessage) {
                        MessageUi.LocalUserMessage(
                            id = it.toString(),
                            content = "This is message number $it from local user",
                            deliveryStatus = ChatMessageDeliveryStatus.SENT,
                            isMenuOpen = false,
                            formattedSentTime = UiText.DynamicString("Friday, Oct 17"),
                        )
                    } else {
                        MessageUi.OtherUserMessage(
                            id = it.toString(),
                            content = "This is message number $it from other user",
                            sender = ChatParticipantUi(
                                id = "2",
                                username = "Jane Doe",
                                initials = "JD",
                            ),
                            formattedSentTime = UiText.DynamicString("Friday, Oct 17"),
                        )
                    }
                }
            ),
            isDetailPresent = true,
            onAction = { },
        )
    }
}