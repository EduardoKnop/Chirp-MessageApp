package com.plcoding.chat.presentation.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.no_chat_selected
import chirp.feature.chat.presentation.generated.resources.select_a_chat
import com.plcoding.chat.domain.models.ChatMessage
import com.plcoding.chat.domain.models.ChatMessageDeliveryStatus
import com.plcoding.chat.presentation.chat_detail.components.DateChip
import com.plcoding.chat.presentation.chat_detail.components.MessageBannerListener
import com.plcoding.chat.presentation.chat_detail.components.MessageBox
import com.plcoding.chat.presentation.chat_detail.components.MessageList
import com.plcoding.chat.presentation.chat_detail.components.PaginationScrollListener
import com.plcoding.chat.presentation.chat_list.components.ChatDetailHeader
import com.plcoding.chat.presentation.components.ChatHeader
import com.plcoding.chat.presentation.components.EmptySection
import com.plcoding.chat.presentation.model.ChatUi
import com.plcoding.chat.presentation.model.MessageUi
import com.plcoding.core.designsystem.components.avatar.ChatParticipantUi
import com.plcoding.core.designsystem.theme.ChirpTheme
import com.plcoding.core.designsystem.theme.extended
import com.plcoding.core.presentation.util.ObserveAsEvents
import com.plcoding.core.presentation.util.UiText
import com.plcoding.core.presentation.util.clearFocusOnTap
import com.plcoding.core.presentation.util.currentDeviceConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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
    onChatMembersClick: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }
    val messageListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ChatDetailEvent.OnChatLeft -> onBack()
            ChatDetailEvent.OnNewMessageSent -> {
                scope.launch {
                    messageListState.animateScrollToItem(0)
                }
            }
            is ChatDetailEvent.OnError -> {
                snackbarState.showSnackbar(event.error.asStringAsync())
            }
        }
    }
    
    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
        if (chatId != null) {
            messageListState.scrollToItem(0)
        }
    }
    
    BackHandler(
        enabled = !isDetailPresent,
    ) {
        scope.launch {
            // Add artificial delay to prevent detail back animation showing
            // an unselected chat the moment we go back
            delay(300)
            viewModel.onAction(ChatDetailAction.OnSelectChat(null))
        }
        onBack()
    }
    
    ChatDetailScreen(
        state = state,
        messageListState = messageListState,
        isDetailPresent = isDetailPresent,
        onAction = { action ->
            when (action) {
                is ChatDetailAction.OnChatMembersClick -> onChatMembersClick()
                is ChatDetailAction.OnBackClick -> onBack()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarState = snackbarState,
    )
}

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    messageListState: LazyListState,
    isDetailPresent: Boolean,
    snackbarState: SnackbarHostState,
    onAction: (ChatDetailAction) -> Unit,
) {
    val configuration = currentDeviceConfiguration()
    val realMessageItemCount = remember(state.messages) {
        state
            .messages
            .filter { it is MessageUi.LocalUserMessage || it is MessageUi.OtherUserMessage }
            .size
    }
    val density = LocalDensity.current
    var headerHeight by remember { mutableStateOf(0.dp) }
    
    LaunchedEffect(messageListState) {
        snapshotFlow {
            messageListState.firstVisibleItemIndex to messageListState.layoutInfo.totalItemsCount
        }.filter { (firstVisibleIndex, totalItemCount) ->
            firstVisibleIndex >= 0 && totalItemCount > 0
        }.collect { (firstVisibleItemIndex, _) ->
            onAction(ChatDetailAction.OnFirstVisibleIndexChanged(firstVisibleItemIndex))
        }
    }
    
    MessageBannerListener(
        lazyListState = messageListState,
        messages = state.messages,
        isBannerVisible = state.bannerState.isVisible,
        onShowBanner = { index ->
            onAction(ChatDetailAction.OnTopVisibleIndexChanged(index))
        },
        onHideBanner = { onAction(ChatDetailAction.OnHideBanner) },
    )
    
    PaginationScrollListener(
        lazyListState = messageListState,
        itemCount = realMessageItemCount,
        isPaginationLoading = state.isPaginationLoading,
        isEndReached = state.endReached,
        onNearTop = { onAction(ChatDetailAction.OnScrollToTop) },
    )
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        contentColor = if (!configuration.isWideScreen) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.extended.surfaceLower
        },
        snackbarHost = {
            SnackbarHost(snackbarState)
        }
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
                    if (state.chatUi == null) {
                        EmptySection(
                            title = stringResource(Res.string.no_chat_selected),
                            description = stringResource(Res.string.select_a_chat),
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        ChatHeader(
                            modifier = Modifier
                                .onSizeChanged {
                                    headerHeight = with(density) {
                                        it.height.toDp()
                                    }
                                }
                        ) {
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
                            messageWithOpenMenu = state.messageWithOpenMenu,
                            listState = messageListState,
                            isPaginationLoading = state.isPaginationLoading,
                            paginationError = state.paginationError?.asString(),
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
                            onPaginationRetryClick = {
                                onAction(ChatDetailAction.OnPaginationRetryClick)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        )
                        
                        AnimatedVisibility(
                            visible = !configuration.isWideScreen,
                        ) {
                            MessageBox(
                                messageTextFieldState = state.messageTextFieldState,
                                isSendButtonEnabled = state.canSendMessage,
                                connectionState = state.connectionState,
                                onSendClick = { onAction(ChatDetailAction.OnSendMessageClick) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 8.dp,
                                        horizontal = 16.dp,
                                    ),
                            )
                        }
                        
                        if (configuration.isWideScreen) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        AnimatedVisibility(
                            visible = configuration.isWideScreen,
                        ) {
                            DynamicRoundedCornerColumn(
                                isCornersRounded = configuration.isWideScreen,
                            ) {
                                MessageBox(
                                    messageTextFieldState = state.messageTextFieldState,
                                    isSendButtonEnabled = state.canSendMessage,
                                    connectionState = state.connectionState,
                                    onSendClick = { onAction(ChatDetailAction.OnSendMessageClick) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                )
                            }
                        }
                    }
                }
            }
            
            AnimatedVisibility(
                visible = state.bannerState.isVisible,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = headerHeight + 16.dp),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                if (state.bannerState.formattedDate != null) {
                    DateChip(
                        date = state.bannerState.formattedDate.asString(),
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
                elevation = if (isCornersRounded) 8.dp else 0.dp,
                shape = if (isCornersRounded) RoundedCornerShape(24.dp) else RectangleShape,
                spotColor = Color.Black.copy(alpha = 0.2f),
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = if (isCornersRounded) RoundedCornerShape(24.dp) else RectangleShape,
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
            snackbarState = remember { SnackbarHostState() },
            messageListState = rememberLazyListState(),
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
                        deliveryStatus = ChatMessageDeliveryStatus.SENT,
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
            snackbarState = remember { SnackbarHostState() },
            messageListState = rememberLazyListState(),
        )
    }
}