@file:OptIn(ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.smartcourse.ui.screens.chat.ChatViewModel


@Composable
fun ChatScreen(
    navController: NavController,
    chatVM: ChatViewModel,
    myId: String
) {
    val chatId = chatVM.chatId
    //val myId = authRepo.currentUser.value?.getUID() ?: return

    var input by rememberSaveable { mutableStateOf("") }
    var otherId by remember { mutableStateOf<String?>(null) }

    var thisUser by remember { mutableStateOf<User?>(null) }
    var otherUser by remember { mutableStateOf<User?>(null) }

    val listState = rememberLazyListState()
    val messages by chatVM.messages.collectAsState()


    /** ONE lifecycle-controlled init block */
    LaunchedEffect(chatId) {
        chatVM.ensureFirebaseReady()

        val receiver = chatVM.getReceiverId(chatId, myId)
        otherId = receiver

        val (me, other) = resolveUsers(myId, chatVM.getBothUsers(chatId))
        thisUser = me
        otherUser = other

        chatVM.startListening(chatId)
    }

    /** STOP listener on exit */
    DisposableEffect(chatId) {
        onDispose {
            chatVM.stopListening()
        }
    }

    /** Auto-scroll */
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
        listState = listState
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
    listState: LazyListState
) {
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ChatTopBar(
                navController = navController,
                otherUser = otherUser
            )
        },
        bottomBar = {
            ChatInputBar(
                input = input,
                onInputChange = { input = it },
                onSend = {
                    val receiver = otherId ?: return@ChatInputBar
                    if (input.isBlank()) return@ChatInputBar

                    chatVM.sendMessage(
                        chatId = chatId,
                        text = input,
                        myId = myId,
                        otherId = receiver
                    )
                    input = ""
                }
            )
        }
    ) { paddingValues ->

        ChatMessageList(
            messages = messages,
            myId = myId,
            listState = listState,
            modifier = Modifier
                .fillMaxSize()
                // ⬇️ IMPORTANT: remove TOP padding, keep others
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = paddingValues.calculateBottomPadding()
                )
        )
    }
}


/** Resolve users helper */
fun resolveUsers(myId: String, users: List<User?>): Pair<User?, User?> {
    val u1 = users.getOrNull(0)
    val u2 = users.getOrNull(1)
    val me = if (u1?.getUID() == myId) u1 else u2
    val other = if (u1?.getUID() == myId) u2 else u1
    return me to other
}

/* ---------------- TOP BAR ---------------- */

@Composable
fun ChatTopBar(
    navController: NavController,
    otherUser: User?
) {
    TopAppBar(
        title = {
            CustomRow(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomImage(
                    imageUrl = otherUser?.image,
                    size = 40.dp
                )

                CustomSpacer(width = 12)

                CustomText(
                    text = otherUser?.name ?: "Loading...",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

/* ---------------- MESSAGE LIST ---------------- */

@Composable
fun ChatMessageList(
    messages: List<Message>,
    myId: String,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(messages, key = { it.messageId }) { msg ->
            MessageRow(
                isMine = msg.senderId == myId,
                text = msg.text
            )
        }
    }
}

@Composable
private fun MessageRow(
    isMine: Boolean,
    text: String
) {
    val bg = if (isMine)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    val fg = if (isMine)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    val alignEnd = isMine || isRtlText(text)

    CustomRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement =
            if (alignEnd) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bg,
            shape = MaterialTheme.shapes.medium
        ) {
            CustomText(
                text = text,
                color = fg,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}


fun isRtlText(text: String): Boolean {
    return text.any {
        Character.getDirectionality(it) ==
                Character.DIRECTIONALITY_RIGHT_TO_LEFT
    }
}



// -----------------------------------------------------------------------------
// INPUT BAR (WhatsApp style)
// -----------------------------------------------------------------------------
@Composable
fun ChatInputBar(
    input: String, onInputChange: (String) -> Unit, onSend: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    CustomRow(
        Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        // ATTACH MENU BUTTON
        CustomBox {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach")
            }

            DropdownMenu(
                expanded = showMenu, onDismissRequest = { showMenu = false }) {

                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Default.PhotoCamera, null) },
                    text = { Text("Camera") },
                    onClick = { showMenu = false })

                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Default.Image, null) },
                    text = { Text("Gallery") },
                    onClick = { showMenu = false })

                DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    text = { Text("Location") },
                    onClick = { showMenu = false })

                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.InsertDriveFile,
                            null
                        )
                    },
                    text = { Text("File") },
                    onClick = { showMenu = false })
            }
        }

        // MESSAGE INPUT FIELD
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp,
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 4
            )
        }

        CustomSpacer(width = 8)

        // SEND BUTTON
        IconButton(onClick = onSend) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
        }
    }
}
