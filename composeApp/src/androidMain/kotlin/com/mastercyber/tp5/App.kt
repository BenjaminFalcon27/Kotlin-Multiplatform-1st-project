package com.mastercyber.tp5

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

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
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            image?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                val silhouetteBitmap = generatePokemonSilhouette(bitmap)
                Image(
                    bitmap = silhouetteBitmap.asImageBitmap(),
                    contentDescription = "Image of a Pokemon",
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
            Text(
                text = name ?: "Loading...",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color(0xFF2C3E50)
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

fun generatePokemonSilhouette(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val silhouetteBitmap = Bitmap.createBitmap(width * 2, height * 2, Bitmap.Config.ARGB_8888)

    val contourColor = 0xFF2C3E50.toInt()
    val fillColor = 0xFF95A5A6.toInt()

    val transparentThreshold = 50
    val contourThreshold = 100

    val visited = Array(height) { BooleanArray(width) }

    fun isTransparent(pixel: Int): Boolean {
        val alpha = (pixel shr 24) and 0xFF
        return alpha < transparentThreshold
    }

    fun fillInside(x: Int, y: Int) {
        val stack = mutableListOf(Pair(x, y))
        while (stack.isNotEmpty()) {
            val (cx, cy) = stack.removeAt(stack.size - 1)
            if (cx in 0 until width && cy in 0 until height && !visited[cy][cx]) {
                visited[cy][cx] = true
                val pixel = bitmap.getPixel(cx, cy)
                if (!isTransparent(pixel)) {
                    silhouetteBitmap.setPixel(cx * 2, cy * 2, fillColor)
                    silhouetteBitmap.setPixel((cx * 2) + 1, cy * 2, fillColor)
                    silhouetteBitmap.setPixel(cx * 2, (cy * 2) + 1, fillColor)
                    silhouetteBitmap.setPixel((cx * 2) + 1, (cy * 2) + 1, fillColor)
                    stack.add(Pair(cx + 1, cy))
                    stack.add(Pair(cx - 1, cy))
                    stack.add(Pair(cx, cy + 1))
                    stack.add(Pair(cx, cy - 1))
                }
            }
        }
    }

    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = bitmap.getPixel(x, y)
            if (isTransparent(pixel)) continue

            val isEdge =
                (x == 0 || y == 0 || x == width - 1 || y == height - 1 ||
                        isTransparent(bitmap.getPixel(x + 1, y)) ||
                        isTransparent(bitmap.getPixel(x - 1, y)) ||
                        isTransparent(bitmap.getPixel(x, y + 1)) ||
                        isTransparent(bitmap.getPixel(x, y - 1)))

            if (isEdge) {
                silhouetteBitmap.setPixel(x * 2, y * 2, contourColor)
                silhouetteBitmap.setPixel((x * 2) + 1, y * 2, contourColor)
                silhouetteBitmap.setPixel(x * 2, (y * 2) + 1, contourColor)
                silhouetteBitmap.setPixel((x * 2) + 1, (y * 2) + 1, contourColor)
            } else {
                if (!visited[y][x]) {
                    fillInside(x, y)
                }
            }
        }
    }

    return silhouetteBitmap
}
