package com.smartcourse.ui.screens.chat.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun MessageBubble(
    isMine: Boolean,
    text: String?,
    type: String = "text"
) {
    if (text.isNullOrBlank()) return

    val context = LocalContext.current
    val chatPalette = LocalAppPalette.current.chatRoom
    val bubbleColor = if (isMine) chatPalette.outgoingBubble else chatPalette.incomingBubble

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isMine) 96.dp else 12.dp,
                end = if (isMine) 12.dp else 96.dp,
                top = 4.dp, // Slightly adjusted for standard feel
                bottom = 4.dp
            ),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMine) 18.dp else 2.dp,
                bottomEnd = if (isMine) 2.dp else 18.dp
            ),
            border = if (!isMine) BorderStroke(1.dp, chatPalette.incomingBorder) else null,
            modifier = Modifier.clickable(enabled = type == "location") {
                val mapUri = Uri.parse("geo:$text?q=$text(Shared+Location)")
                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                try {
                    context.startActivity(mapIntent)
                } catch (e: Exception) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, mapUri))
                }
            }
        ) {
            when (type) {
                "image" -> {
                    AsyncImage(
                        model = text,
                        contentDescription = "Shared image",
                        modifier = Modifier
                            .sizeIn(maxWidth = 250.dp, maxHeight = 400.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                "location" -> {
                    LocationContent(text)
                }
                else -> {
                    Text(
                        text = text.trim(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color.White // Keeps readability on your dark theme
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationContent(locationData: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Location Shared", color = Color.White)
        Text(text = locationData, color = Color.White.copy(alpha = 0.7f))
    }
}