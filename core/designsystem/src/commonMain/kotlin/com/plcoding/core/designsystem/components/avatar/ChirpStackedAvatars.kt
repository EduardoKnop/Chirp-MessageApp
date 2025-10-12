package com.plcoding.core.designsystem.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.plcoding.core.designsystem.theme.ChirpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChirpStackedAvatars(
    avatars: List<ChatParticipantUi>,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.SMALL,
    maxDisplayed: Int = 2,
    overlapPercentage: Float = 0.4f,
) {
    val overlapOffset = -(size.dp * overlapPercentage)
    val visibleAvatars = avatars.take(maxDisplayed)
    val remainingCount = (avatars.size - maxDisplayed).coerceAtLeast(0)
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(overlapOffset),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        visibleAvatars.forEach { avatarUi ->
            ChirpAvatarPhoto(
                displayText = avatarUi.initials,
                size = size,
                imageUrl = avatarUi.imageUrl,
            )
        }
        
        if (remainingCount == 1) {
            ChirpAvatarPhoto(
                displayText = "$remainingCount+",
                size = size,
                textColor = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview
@Composable
private fun ChirpStackedAvatarsPreview() {
    ChirpTheme {
        ChirpStackedAvatars(
            avatars = listOf(
                ChatParticipantUi(
                    id = "1",
                    username = "user1",
                    initials = "U1",
                    imageUrl = null
                ),
                ChatParticipantUi(
                    id = "2",
                    username = "user2",
                    initials = "U2",
                    imageUrl = null
                ),
                ChatParticipantUi(
                    id = "3",
                    username = "user3",
                    initials = "U3",
                    imageUrl = null
                ),
            )
        )
    }
}