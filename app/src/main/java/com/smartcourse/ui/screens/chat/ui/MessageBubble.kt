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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun MessageBubble(
    isMine: Boolean,
    text: String?,
    type: String = "text"
) {
    if (text.isNullOrBlank()) return

    val context = LocalContext.current
    val chat = LocalAppPalette.current.chatRoom
    val bg = if (isMine) chat.outgoingBubble else chat.incomingBubble

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isMine) 96.dp else 12.dp,
                end = if (isMine) 12.dp else 96.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bg,
            shape = RoundedCornerShape(18.dp),
            border = if (!isMine) BorderStroke(1.dp, chat.incomingBorder) else null,
            // Functional Google Maps Click logic
            modifier = Modifier.clickable(enabled = type == "location") {
                if (type == "location") {
                    val mapUri = Uri.parse("geo:$text?q=$text(Shared+Location)")
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    try {
                        context.startActivity(mapIntent)
                    } catch (e: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, mapUri))
                    }
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
                    LocationBubbleContent(text)
                }
                else -> {
                    CustomText(
                        text = text.trim(),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LocationBubbleContent(locationData: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.White
        )
        CustomText(text = "Location Shared")
        CustomText(text = locationData)
    }
}