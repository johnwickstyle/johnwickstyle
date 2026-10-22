package com.example.universalgallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.universalgallery.ui.main.MainScreen
import com.example.universalgallery.ui.theme.UniversalGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniversalGalleryTheme {
                MainScreen()
            }
        }
    }
}
