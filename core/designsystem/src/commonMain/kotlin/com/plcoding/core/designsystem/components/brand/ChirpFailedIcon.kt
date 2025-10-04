package com.plcoding.core.designsystem.components.brand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.plcoding.core.designsystem.theme.ChirpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChirpFailedIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun ChirpFailedIconPreview() {
    ChirpTheme {
        ChirpFailedIcon()
    }
}