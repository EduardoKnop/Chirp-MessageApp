package com.plcoding.core.designsystem.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plcoding.core.designsystem.components.brand.ChirpSuccessIcon
import com.plcoding.core.designsystem.components.buttons.ChirpButton
import com.plcoding.core.designsystem.components.buttons.ChirpButtonStyle
import com.plcoding.core.designsystem.theme.ChirpTheme
import com.plcoding.core.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChirpSimpleResultLayout(
    title: String,
    description: String,
    icon: @Composable ColumnScope.() -> Unit,
    primaryButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    secondaryButton: @Composable (() -> Unit)? = null,
    secondaryError: String? = null,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -(25).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            primaryButton()
            
            secondaryButton?.let { button ->
                Spacer(modifier = Modifier.height(8.dp))
                button()
                secondaryError?.let { error ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = error,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
private fun ChirpSimpleSuccessLayoutPreview() {
    ChirpTheme {
        ChirpSimpleResultLayout(
            title = "Hello World",
            description = "This is a simple success layout",
            icon = { ChirpSuccessIcon() },
            primaryButton = {
                ChirpButton(
                    text = "Log In",
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            },
            secondaryButton = {
                ChirpButton(
                    text = "Log In",
                    onClick = { },
                    style = ChirpButtonStyle.SECONDARY,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        )
    }
}