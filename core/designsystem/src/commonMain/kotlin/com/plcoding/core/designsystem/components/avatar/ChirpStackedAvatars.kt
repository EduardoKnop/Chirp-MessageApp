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
    avatars: List<AvatarUi>,
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
                displayText = avatarUi.initialText,
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
fun ChirpStackedAvatarsPreview() {
    ChirpTheme {
        ChirpStackedAvatars(
            avatars = listOf(
                AvatarUi(
                    id = "1",
                    username = "user1",
                    initialText = "U1",
                    imageUrl = null
                ),
                AvatarUi(
                    id = "2",
                    username = "user2",
                    initialText = "U2",
                    imageUrl = null
                ),
                AvatarUi(
                    id = "3",
                    username = "user3",
                    initialText = "U3",
                    imageUrl = null
                ),
            )
        )
    }
}