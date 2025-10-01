package com.plcoding.core.designsystem.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.plcoding.core.designsystem.layout.ChirpAdaptiveResultLayout
import com.plcoding.core.designsystem.theme.ChirpTheme

@Preview(device = Devices.NEXUS_10)
@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun ChirpAdaptiveResultLayoutPreview() {
    ChirpTheme {
        ChirpAdaptiveResultLayout(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Text(
                text = "Registration successful!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}