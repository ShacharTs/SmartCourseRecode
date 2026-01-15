package com.smartcourse.ui.screens.chatlist


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.table.UserTable
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomCard
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomRow
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.theme.LocalAppPalette

/**
 * ChatListScreen is a composable function that displays a list of chats.
 * It uses a LazyColumn to display the list of chats.
 */
@Composable
fun ChatListScreen(
    navController: NavController,
    chatListVM: ChatListViewModel
) {
    val chats by chatListVM.chats.collectAsState()

    LaunchedEffect(Unit) {
        chatListVM.refresh()
    }



    ChatListContent(
        chats = chats,
        navController = navController
    )
}



@Composable
fun ChatListContent(
    chats: List<ChatItem>,
    navController: NavController,
) {
    CustomColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
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

    CustomCard(
        onClick = onClick
    ) {
        CustomRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ChatAvatar(
                imageUrl = imageUrl,
                backgroundColor = palette.chatList.accent
            )

            CustomSpacer(width = 14)

            CustomColumn(
                modifier = Modifier.weight(1f)
            ) {
                CustomText(
                    text = chat.otherUser?.name.orEmpty(),
                    fontSize = 16.sp,
                    color = palette.chatList.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                CustomSpacer(height = 4)

                CustomText(
                    text = chat.lastMessage,
                    fontSize = 14.sp,
                    color = palette.chatList.subtext,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}




@Composable
fun ChatAvatar(
    imageUrl: String?,
    backgroundColor: Color
) {
    CustomBox(
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








