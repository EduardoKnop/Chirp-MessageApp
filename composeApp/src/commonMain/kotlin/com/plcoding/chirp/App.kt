package com.plcoding.chirp

import androidx.compose.runtime.Composable
import com.plcoding.chirp.navigation.NavigationRoot
import com.plcoding.core.designsystem.theme.ChirpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    ChirpTheme {
        NavigationRoot()
    }
}