package com.example.gasfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.gasfinder.ui.GasFinderApp
import com.example.gasfinder.ui.theme.GasFinderTheme

/**
 * Единственная Activity приложения.
 *
 * Интерфейс целиком построен на Jetpack Compose, поэтому Activity только
 * задаёт тему и передаёт управление корневому composable [GasFinderApp].
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            GasFinderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GasFinderApp()
                }
            }
        }
    }
}
