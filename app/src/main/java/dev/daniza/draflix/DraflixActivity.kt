package dev.daniza.draflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.daniza.draflix.ui.screen.DraflixScreen
import dev.daniza.draflix.ui.theme.DraflixTheme

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