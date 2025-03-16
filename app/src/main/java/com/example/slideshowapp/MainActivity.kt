package com.example.slideshowapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.slideshowapp.ui.auth.LoginScreen
import com.example.slideshowapp.ui.slides.SlideShowScreen
import com.example.slideshowapp.ui.theme.SlideShowAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SlideShowAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLoggedIn by remember { mutableStateOf(false) }
                    var isAdmin by remember { mutableStateOf(false) }

                    if (!isLoggedIn) {
                        LoginScreen(
                            onLoginSuccess = { admin ->
                                isLoggedIn = true
                                isAdmin = admin
                            }
                        )
                    } else {
                        SlideShowScreen(isAdmin = isAdmin)
                    }
                }
            }
        }
    }
} 