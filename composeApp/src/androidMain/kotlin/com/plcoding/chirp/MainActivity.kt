package com.plcoding.chirp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.plcoding.chat.database.ChirpChatDatabase
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    
    val db by inject<ChirpChatDatabase>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        var shouldShowSplashScreen = true
        
        installSplashScreen().setKeepOnScreenCondition { shouldShowSplashScreen }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        println(db.toString())

        setContent {
            App(
                onAuthenticationChecked = {
                    shouldShowSplashScreen = false
                },
            )
        }
    }
}

@Preview
@Composable
private fun AppAndroidPreview() {
    App()
}