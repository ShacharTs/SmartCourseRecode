package com.smartcourse.ui.screens.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.table.UserTable
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun ChatListScreen(
    navController: NavController,
    chatListVM: ChatListViewModel = hiltViewModel()
) {
    // Collect properly without calling .value in composition
    val chats by chatListVM.chats.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        chatListVM.refresh()
    }


    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chats) { chat ->
                ChatListItem(
                    chat = chat,
                    imageUrl = chat.otherUser?.image,
                    onClick = {
                        navController.navigate(
                            Screen.ChatRoom.createRoute(chat.chatId)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: ChatItem,
    imageUrl: String?,
    onClick: () -> Unit
) {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChatAvatar(
            imageUrl = imageUrl,
            backgroundColor = palette.chatList.accent
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.otherUser?.name.orEmpty(),
                fontSize = 16.sp,
                color = palette.chatList.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // avoid to see the photo url
            val isPhoto = chat.lastMessage.startsWith("https://firebasestorage")

            val messagePreview = if (isPhoto) "Photo" else chat.lastMessage

            Text(
                text = messagePreview,
                fontSize = 14.sp,
                color = if (isPhoto) palette.chatList.textPrimary else palette.chatList.subtext,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (isPhoto) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ChatAvatar(
    imageUrl: String?,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = UserTable.IMAGE,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}