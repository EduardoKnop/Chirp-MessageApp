package com.plcoding.chat.presentation.chat_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.cloud_off_icon
import chirp.feature.chat.presentation.generated.resources.send
import chirp.feature.chat.presentation.generated.resources.send_message
import com.plcoding.chat.domain.models.ConnectionState
import com.plcoding.chat.presentation.util.toUiText
import com.plcoding.core.designsystem.components.buttons.ChirpButton
import com.plcoding.core.designsystem.components.textfields.ChirpMultiLineTextField
import com.plcoding.core.designsystem.theme.ChirpTheme
import com.plcoding.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageBox(
    messageTextFieldState: TextFieldState,
    isSendButtonEnabled: Boolean,
    connectionState: ConnectionState,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isConnected = connectionState == ConnectionState.CONNECTED
    val focusRequester = remember { FocusRequester() }
    
    ChirpMultiLineTextField(
        state = messageTextFieldState,
        modifier = modifier
            .focusRequester(focusRequester)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusRequester.requestFocus()
            },
        placeholder = stringResource(Res.string.send_message),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send,
        ),
        onKeyboardAction = onSendClick,
        bottomContent = {
            Spacer(modifier = Modifier.weight(1f))
            if (!isConnected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.cloud_off_icon),
                        contentDescription = connectionState.toUiText().asString(),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.extended.textDisabled,
                    )
                    Text(
                        text = connectionState.toUiText().asString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.extended.textDisabled,
                    )
                }
            }
            ChirpButton(
                text = stringResource(Res.string.send),
                onClick = onSendClick,
                enabled = isConnected && isSendButtonEnabled,
            )
        }
    )
}

@Preview
@Composable
private fun MessageBoxPreview() {
    ChirpTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            MessageBox(
                messageTextFieldState = rememberTextFieldState(),
                isSendButtonEnabled = false,
                connectionState = ConnectionState.DISCONNECTED,
                onSendClick = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}