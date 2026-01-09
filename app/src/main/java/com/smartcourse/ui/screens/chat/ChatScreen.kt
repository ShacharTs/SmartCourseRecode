@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartcourse.data.models.chat.Message
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomImage
import com.smartcourse.ui.screens.components.CustomRow
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun ChatScreen(
    navController: NavController,
    chatVM: ChatViewModel,
    myId: String,
) {
    val chatId = chatVM.chatId
    var otherUser by remember { mutableStateOf<User?>(null) }
    var otherId by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val messages by chatVM.messages.collectAsState()

    LaunchedEffect(chatId) {
        chatVM.ensureFirebaseReady()
        otherId = chatVM.getReceiverId(chatId, myId)
        val (_, other) = resolveUsers(myId, chatVM.getBothUsers(chatId))
        otherUser = other
        chatVM.startListening(chatId)
    }

    DisposableEffect(chatId) {
        onDispose { chatVM.stopListening() }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    ChatScaffold(
        navController = navController,
        otherUser = otherUser,
        otherId = otherId,
        chatVM = chatVM,
        chatId = chatId,
        myId = myId,
        messages = messages,
        listState = listState,
    )
}

@Composable
private fun ChatScaffold(
    navController: NavController,
    otherUser: User?,
    otherId: String?,
    chatVM: ChatViewModel,
    chatId: String,
    myId: String,
    messages: List<Message>,
    listState: LazyListState,
) {
    var input by rememberSaveable { mutableStateOf("") }

    val chat = LocalAppPalette.current.chatRoom

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(chat.backgroundGradient)
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ChatTopBar(
                    navController = navController,
                    otherUser = otherUser,
                )
            },
            bottomBar = {
                ChatInputBar(
                    input = input,
                    onInputChange = { input = it },
                    onSend = {
                        val receiver = otherId ?: return@ChatInputBar
                        if (input.isBlank()) return@ChatInputBar
                        chatVM.sendMessage(chatId, input, myId, receiver)
                        input = ""
                    },
                )
            },
        ) { paddingValues ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding() + 8.dp,
                        bottom = paddingValues.calculateBottomPadding() + 8.dp,
                        start = 12.dp,
                        end = 12.dp
                    ),
                shape = RoundedCornerShape(24.dp),
                color = chat.card.copy(alpha = 0.92f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
            ) {
                ChatMessageList(
                    messages = messages,
                    myId = myId,
                    listState = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp, vertical = 10.dp)
                )
            }
        }
    }
}


@Composable
fun ChatTopBar(
    navController: NavController,
    otherUser: User?,
) {
    val palette = LocalAppPalette.current
    val isDark = palette.isDark

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        title = {
            CustomRow(verticalAlignment = Alignment.CenterVertically) {
                CustomImage(imageUrl = otherUser?.image, size = 40.dp)
                CustomSpacer(width = 12)
                CustomText(
                    text = otherUser?.name ?: "Loading…",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    )

}

@Composable
fun ChatMessageList(
    messages: List<Message>,
    myId: String,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        items(messages, key = { it.messageId }) { msg ->
            MessageBubble(
                isMine = msg.senderId == myId,
                text = msg.text,
            )
        }
    }
}

@Composable
private fun MessageBubble(
    isMine: Boolean,
    text: String,
) {
    if (text.isBlank()) return

    val chat = LocalAppPalette.current.chatRoom

    val bg = if (isMine) chat.outgoingBubble else chat.incomingBubble
    val fg = chat.textPrimary

    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isMine) 96.dp else 12.dp,
                end = if (isMine) 12.dp else 96.dp,
                top = 6.dp,
                bottom = 6.dp,
            ),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
    ) {
        Surface(
            color = bg,
            shape = shape,
            modifier = Modifier.widthIn(max = 260.dp),
            border = if (!isMine)
                BorderStroke(1.dp, chat.incomingBorder)
            else null,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            CustomText(
                text = text.trim(),
                color = fg,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
            )
        }
    }
}


@Composable
fun ChatInputBar(
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val isDark = palette.isDark

    val pillBg = if (isDark) Color(0xFF241636) else Color(0xFFF3E8FF)
    val attachBg = if (isDark) Color(0xFF241636) else Color(0xFFEDE9FE)
    val sendBg = if (isDark) Color(0xFFEC4899) else Color(0xFF7C3AED)

    val textColor = if (isDark) Color.White else Color(0xFF1F2937)
    val placeholderColor = if (isDark) Color(0xFFA9A9B3) else Color(0xFF6B7280)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = attachBg.copy(alpha = 0.9f),
            shape = CircleShape,
            modifier = Modifier.size(44.dp),
        ) {
            CustomBox(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFFB388FF) else Color(0xFF7C3AED),
                )
            }
        }

        CustomSpacer(width = 12)

        Surface(
            color = pillBg.copy(alpha = if (isDark) 0.75f else 0.85f),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.weight(1f),
        ) {
            TextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = { Text("Message", color = placeholderColor) },
                maxLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = if (isDark) Color(0xFFEC4899) else Color(0xFF7C3AED),
                ),
            )
        }

        CustomSpacer(width = 12)

        Surface(
            color = sendBg,
            shape = CircleShape,
            modifier = Modifier.size(44.dp),
        ) {
            IconButton(onClick = onSend) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
    }
}

fun resolveUsers(myId: String, users: List<User?>): Pair<User?, User?> {
    val u1 = users.getOrNull(0)
    val u2 = users.getOrNull(1)
    val me = if (u1?.getUID() == myId) u1 else u2
    val other = if (u1?.getUID() == myId) u2 else u1
    return me to other
}
