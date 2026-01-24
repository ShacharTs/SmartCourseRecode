package com.smartcourse.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.ui.theme.StudentHomeColorPalette


@Composable
fun LatestChatsSection(
    chats: List<ChatItem>,
    colors: StudentHomeColorPalette
) {
    Text(
        text = "Latest Chats",
        color = colors.textPrimary,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    chats.take(3).forEach { chat ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.card)
                .padding(horizontal = 16.dp)
                .clickable {
                    Log.d("Chat", "Open chatId=${chat.chatId}")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(chat.otherUser?.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.accent)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = "${chat.otherUser?.displayName ?: "Chat"} — ${chat.lastMessage}",
                color = colors.textPrimary,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(10.dp))
    }
}