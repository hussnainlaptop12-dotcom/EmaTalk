package com.example.ematalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ematalk.navigation.AppNavigation
import com.example.ematalk.ui.theme.EmaTalkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EmaTalkTheme {
                AppNavigation()
            }
        }
    }
}