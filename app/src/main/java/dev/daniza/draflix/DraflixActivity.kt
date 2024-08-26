package dev.daniza.draflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dev.daniza.draflix.ui.screen.DraflixScreen
import dev.daniza.draflix.ui.theme.DraflixTheme

@AndroidEntryPoint
class DraflixActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DraflixTheme {
                DraflixScreen()
            }
        }
    }
}