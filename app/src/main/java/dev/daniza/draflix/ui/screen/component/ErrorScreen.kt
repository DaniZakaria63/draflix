package dev.daniza.draflix.ui.screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.daniza.draflix.R

@Composable
fun ErrorScreen(
    message: String = "",
    retry: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.img_error),
                contentDescription = "error",
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "Terjadi kesalahan, silahkan coba lagi",
                color = Color.Black,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))

            if (message.isNotEmpty()) {
                Text(
                    text = "Terjadi kesalahan, silahkan coba lagi",
                    color = Color.Black,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Button(
                onClick = retry,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text("Ulangi")
            }
        }
    }
}