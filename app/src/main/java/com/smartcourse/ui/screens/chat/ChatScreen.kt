@file:OptIn(ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.chat.Message
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomImage
import com.smartcourse.ui.screens.components.CustomRow
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.viewmodels.ChatViewModel


@Composable
fun ChatScreen(
    navController: NavController ,chatVM: ChatViewModel, authVM: AuthViewModel
) {

    val chatId = chatVM.chatId

    val myId = authVM.user?.getUID() ?: ""

    var input by remember { mutableStateOf("") }
    var otherId by remember { mutableStateOf("") }

    var thisUser by remember { mutableStateOf<User?>(null) }
    var otherUser by remember { mutableStateOf<User?>(null) }

    // Resolve me / other from Supabase user list
    fun resolveUsers(myId: String, users: List<User?>): Pair<User?, User?> {
        val u1 = users.getOrNull(0)
        val u2 = users.getOrNull(1)

        val me = if (u1?.getUID() == myId) u1 else u2
        val other = if (u1?.getUID() == myId) u2 else u1

        return Pair(me, other)
    }

    // Init firebase + load users
    LaunchedEffect(chatId) {
        chatVM.ensureFirebaseReady()

        // Firebase: find other user id
        otherId = chatVM.getReceiverId(chatId, myId)

        // Supabase: load both users
        val (me, other) = resolveUsers(myId, chatVM.getBothUsers(chatId))
        thisUser = me
        otherUser = other
    }

    // Listen to chat messages
    LaunchedEffect(Unit) { chatVM.startListening(chatId) }

    val messages by chatVM.messages.collectAsState()

    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new messages
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) listState.scrollToItem(0)
    }

    Scaffold(topBar = {
        ChatTopBar(
            navController = navController, otherUser = otherUser
        )
    }, bottomBar = {
        ChatInputBar(input = input, onInputChange = { input = it }, onSend = {
            if (input.isNotBlank()) {
                chatVM.sendMessage(
                    chatId = chatId, text = input, myId = myId, otherId = otherId
                )
                input = ""
            }
        })
    }) { innerPadding ->

        ChatMessageList(
            messages = messages,
            myId = myId,
            listState = listState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}


@Composable
fun ChatTopBar(
    navController: NavController, otherUser: User?
) {
    TopAppBar(title = {
        CustomRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 20.dp)
                .fillMaxWidth()
                .clickable(onClick = {
                    //todo send to profile + make a profile screen for view
                })
        ) {
            CustomImage(
                imageUrl = otherUser?.image, size = 40.dp
            )

            CustomSpacer(modifier = Modifier.width(12.dp))

            CustomText(
                text = otherUser?.name ?: "Loading...",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }, navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
            )
        }
    })
}


@Composable
fun ChatMessageList(
    messages: List<Message>, myId: String, listState: LazyListState, modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier, state = listState, reverseLayout = true
    ) {
        items(messages) { msg ->
            MessageRow(
                isMine = msg.senderId == myId, text = msg.text
            )
        }
    }
}


@Composable
private fun MessageRow(
    isMine: Boolean, text: String
) {
    val bubbleColor = if (isMine) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (isMine) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    CustomRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(4.dp)
        ) {
            CustomText(
                text = text, modifier = Modifier.padding(12.dp), color = textColor
            )
        }
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
