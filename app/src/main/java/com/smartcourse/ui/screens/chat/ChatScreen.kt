@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
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

    Scaffold(
        containerColor = LocalAppPalette.current.chatRoom.background,
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
        ChatMessageList(
            messages = messages,
            myId = myId,
            listState = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = paddingValues.calculateBottomPadding(),
                ),
        )
    }
}

@Composable
fun ChatTopBar(
    navController: NavController,
    otherUser: User?,
) {
    TopAppBar(
        title = {
            CustomRow(verticalAlignment = Alignment.CenterVertically) {
                CustomImage(imageUrl = otherUser?.image, size = 40.dp)
                CustomSpacer(width = 12)
                CustomText(
                    text = otherUser?.name ?: "Loading…",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
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

    val colors = LocalAppPalette.current.chatRoom

    val bg = if (isMine) colors.accent else colors.card
    val fg = colors.textPrimary

    val shape = if (isMine) {
        RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomEnd = 4.dp,
            bottomStart = 18.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomEnd = 18.dp,
            bottomStart = 4.dp
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isMine) 96.dp else 12.dp,
                end = if (isMine) 12.dp else 96.dp,
                top = 4.dp,
                bottom = 4.dp
            ),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bg,
            shape = shape,
            modifier = Modifier.widthIn(max = 220.dp)
        ) {
            CustomText(
                text = text.trim(),
                color = fg,
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
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
    var showMenu by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    Surface(color = colors.surface) {
        CustomRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomBox {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.AttachFile, null)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Camera") },
                        leadingIcon = { Icon(Icons.Default.PhotoCamera, null) },
                        onClick = { showMenu = false },
                    )
                    DropdownMenuItem(
                        text = { Text("Gallery") },
                        leadingIcon = { Icon(Icons.Default.Image, null) },
                        onClick = { showMenu = false },
                    )
                    DropdownMenuItem(
                        text = { Text("Location") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        onClick = { showMenu = false },
                    )
                    DropdownMenuItem(
                        text = { Text("File") },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.InsertDriveFile, null)
                        },
                        onClick = { showMenu = false },
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f),
            ) {
                TextField(
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = { Text("Message") },
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }

            CustomSpacer(width = 8)

            IconButton(onClick = onSend) {
                Icon(Icons.AutoMirrored.Filled.Send, null)
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

fun isRtlText(text: String): Boolean =
    text.any {
        Character.getDirectionality(it) ==
                Character.DIRECTIONALITY_RIGHT_TO_LEFT
    }
