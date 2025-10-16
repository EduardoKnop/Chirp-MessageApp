package com.plcoding.chat.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.plcoding.core.designsystem.components.avatar.ChatParticipantUi
import com.plcoding.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.plcoding.core.designsystem.components.brand.ChirpHorizontalDivider
import com.plcoding.core.designsystem.theme.ChirpTheme
import com.plcoding.core.designsystem.theme.extended
import com.plcoding.core.designsystem.theme.titleXSmall
import com.plcoding.core.presentation.util.DeviceConfiguration
import com.plcoding.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ColumnScope.ChatParticipantsSelectionSection(
    existingParticipants: List<ChatParticipantUi>,
    selectedParticipants: List<ChatParticipantUi>,
    modifier: Modifier = Modifier,
    searchResult: ChatParticipantUi? = null,
) {
    val deviceConfiguration = currentDeviceConfiguration()
    val rootHeightModifier = when (deviceConfiguration) {
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Modifier
                .animateContentSize()
                .heightIn(min = 200.dp, max = 300.dp)
        }
        else -> Modifier.weight(1f)
    }
    
    Box(
        modifier = rootHeightModifier
            .then(modifier)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(
                items = existingParticipants,
                key = { "existing_${it.id}" },
            ) { participant ->
                ChatParticipantListItem(
                    participant = participant,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            
            if (existingParticipants.isNotEmpty()) {
                item {
                    ChirpHorizontalDivider()
                }
            }
            
            searchResult?.let {
                item {
                    ChatParticipantListItem(
                        participant = searchResult,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            
            if (selectedParticipants.isNotEmpty() && searchResult == null) {
                items(
                    items = selectedParticipants,
                    key = { it.id },
                ) { participant ->
                    ChatParticipantListItem(
                        participant = participant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun ChatParticipantListItem(
    participant: ChatParticipantUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ChirpAvatarPhoto(
            displayText = participant.initials,
            imageUrl = participant.imageUrl,
        )
        Text(
            text = participant.username,
            style = MaterialTheme.typography.titleXSmall,
            color = MaterialTheme.colorScheme.extended.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun ChatParticipantsSelectionSectionPreview() {
    ChirpTheme {
        Column {
            ChatParticipantsSelectionSection(
                existingParticipants = List(3) {
                    ChatParticipantUi(
                        id = it.toString(),
                        username = "Existing $it",
                        initials = "E$it",
                        imageUrl = null,
                    )
                },
                selectedParticipants = List(10) {
                    ChatParticipantUi(
                        id = it.toString(),
                        username = "User $it",
                        initials = "U$it",
                        imageUrl = null,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}