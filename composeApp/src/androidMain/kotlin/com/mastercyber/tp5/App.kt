package com.mastercyber.tp5

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var nameAndImage by remember { mutableStateOf(Pair<String?, ByteArray?>(null, null)) }
        var name by remember { mutableStateOf<String?>(null) }
        var image by remember { mutableStateOf<ByteArray?>(null) }

        LaunchedEffect(Unit) {
            nameAndImage = Pokemon().fetchPokemonNameAndImage()
            name = nameAndImage.first
            image = nameAndImage.second
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            image?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Image of a Pokemon",
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
            Text(
                text = name ?: "Loading...",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
