package com.plcoding.chat.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.plcoding.chat.presentation.chat_list_detail.ChatListDetailAdaptiveLayout
import kotlinx.serialization.Serializable

sealed interface ChatGraphRoutes {
    
    @Serializable
    data object Graph : ChatGraphRoutes
    
    @Serializable
    data object ChatListDetail: ChatGraphRoutes
}

fun NavGraphBuilder.chatGraph(
    navController: NavController,
) {
    navigation<ChatGraphRoutes.Graph>(
        startDestination = ChatGraphRoutes.ChatListDetail,
    ) {
        composable<ChatGraphRoutes.ChatListDetail> {
            ChatListDetailAdaptiveLayout()
        }
    }
}