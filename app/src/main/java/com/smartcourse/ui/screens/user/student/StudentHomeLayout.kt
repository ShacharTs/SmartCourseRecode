package com.smartcourse.ui.screens.user.student

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.StudentHomeColorPalette

/* =========================================================
   BASIC COMPONENTS
   ========================================================= */

@Composable
fun TutorAvatar(
    user: User, // Changed from Tutor to User
    colors: StudentHomeColorPalette,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.image) // Direct access
                .crossfade(true).build(),
            contentDescription = "Tutor avatar",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.accent)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            user.displayName, // Uses the User helper property
            color = colors.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ActionButton(
    text: String, isPrimary: Boolean, colors: StudentHomeColorPalette, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPrimary) colors.accent else colors.card)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

/* =========================================================
   DISCOVER CARD
   ========================================================= */

@Composable
fun TutorDiscoverCard(
    user: User, // Changed from Tutor to User
    colors: StudentHomeColorPalette,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(156.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .clickable { onProfileClick() }
            .padding(12.dp)) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.image) // Direct access
                            .crossfade(true).build(),
                        contentDescription = "Tutor avatar",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.accent)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        // Access transient courses list from the flat User model
                        user.courses.take(2).forEach { course ->
                            Text(
                                text = course.name,
                                color = colors.subtext,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (user.courses.size > 2) {
                            Text(
                                text = "+${user.courses.size - 2} more",
                                color = colors.subtext,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton("Chat", true, colors) { onChatClick() }
                ActionButton("Save", false, colors) { onSaveClick() }
            }
        }
    }
}


/* =========================================================
   SECTIONS
   ========================================================= */

@Composable
fun MyTutorsSection(
    tutors: List<User>, // Changed to List<User>
    colors: StudentHomeColorPalette,
    onChatClick: (User) -> Unit
) {
    Text(
        "My Tutors",
        color = colors.textPrimary,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(10.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
        // items import is required to avoid Int mismatch errors
        items(tutors) { tutor ->
            TutorAvatar(
                user = tutor,
                colors = colors,
                onClick = { onChatClick(tutor) }
            )
        }
    }
}

@Composable
fun DiscoverTutorsSection(
    tutors: List<User>, // Changed to List<User>
    colors: StudentHomeColorPalette,
    onProfileClick: (User) -> Unit,
    onChatClick: (User) -> Unit,
    onSaveClick: (User) -> Unit
) {
    Text(
        "Discover Tutors",
        color = colors.textPrimary,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(tutors.take(4)) { tutor ->
            TutorDiscoverCard(
                user = tutor,
                colors = colors,
                onProfileClick = { onProfileClick(tutor) },
                onChatClick = { onChatClick(tutor) },
                onSaveClick = { onSaveClick(tutor) }
            )
        }
    }
}

@Composable
fun LatestChatsSection(
    chats: List<ChatItem>,
    colors: StudentHomeColorPalette
) {
    Text(
        "Latest Chats",
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
                    .crossfade(true).build(),
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.accent),
            )

            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "${chat.otherUser?.displayName ?: "Chat"} — ${chat.lastMessage}",
                color = colors.textPrimary,
                fontSize = 15.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

/* =========================================================
   MAIN SCREEN
   ========================================================= */

@Composable
fun StudentHomeLayout(
    navController: NavController,
    user: User
) {
    val vm: StudentHomeViewModel = hiltViewModel()

    val myTutors by vm.myTutors
    val discoverTutors by vm.discoverTutors
    val chats by vm.latestChats

    val palette = LocalAppPalette.current
    val homeColors = palette.home

    // Use the flat userId property
    LaunchedEffect(user.userId) {
        vm.load(user)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MyTutorsSection(
                tutors = myTutors,
                colors = homeColors,
                onChatClick = { tutor ->
                    vm.openChatWithTutor(
                        tutorId = tutor.userId, // Direct access
                        navController = navController
                    )
                }
            )
        }

        item {
            DiscoverTutorsSection(
                tutors = discoverTutors,
                colors = homeColors,
                onProfileClick = { tutor ->
                    navController.navigate(
                        Screen.ShowOtherProfile.createRoute(tutor.userId) // Direct access
                    )
                },
                onChatClick = { tutor ->
                    vm.openChatWithTutor(
                        tutorId = tutor.userId,
                        navController = navController
                    )
                },
                onSaveClick = { tutor ->
                    vm.saveUser(tutor) // Pass the User directly
                }
            )
        }

        item { LatestChatsSection(chats, homeColors) }
    }
}