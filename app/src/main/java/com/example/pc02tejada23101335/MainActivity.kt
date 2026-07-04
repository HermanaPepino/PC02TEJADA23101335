package com.example.pc02tejada23101335

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pc02tejada23101335.presentation.app.CurrencyApp
import com.example.pc02tejada23101335.ui.theme.PC02TEJADA23101335Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PC02TEJADA23101335Theme {
                CurrencyApp()
            }
        }
    }
}