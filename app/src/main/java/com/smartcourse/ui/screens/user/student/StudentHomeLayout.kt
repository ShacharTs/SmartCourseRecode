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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.data.models.usermodel.Tutor
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.StudentHomeColorPalette

/* =========================================================
   BASIC COMPONENTS
   ========================================================= */

@Composable
fun TutorAvatar(
    tutor: Tutor,
    colors: StudentHomeColorPalette,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(colors.accent)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            tutor.user.getUserName(), // name is nullable -> use safe getter
            color = colors.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            tutor.teachingCourses.firstOrNull()?.name ?: "",
            color = colors.subtext,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    isPrimary: Boolean,
    colors: StudentHomeColorPalette,
    onClick: () -> Unit
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
    tutor: Tutor,
    colors: StudentHomeColorPalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(156.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.accent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            tutor.user.getUserName(),
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            tutor.teachingCourses.firstOrNull()?.name ?: "",
                            color = colors.subtext,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton("Chat", true, colors) {
                    Log.d("Chat", "Chat with tutorId=${tutor.user.getUID()}")
                    onClick()
                }
                ActionButton("Save", false, colors) {
                    Log.d("Save", "Save tutorId=${tutor.user.getUID()}")
                }
            }
        }
    }
}

/* =========================================================
   SECTIONS
   ========================================================= */

@Composable
fun MyTutorsSection(
    tutors: List<Tutor>,
    colors: StudentHomeColorPalette
) {
    Text("My Tutors", color = colors.textPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(10.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
        items(tutors) { tutor ->
            TutorAvatar(tutor = tutor, colors = colors) {
                Log.d("MyTutors", "Tutor clicked: ${tutor.user.getUID()}")
            }
        }
    }
}

@Composable
fun DiscoverTutorsSection(
    tutors: List<Tutor>,
    colors: StudentHomeColorPalette
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
            TutorDiscoverCard(tutor = tutor, colors = colors) {
                Log.d("Discover", "Tutor card clicked: ${tutor.user.getUID()}")
            }
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
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.accent)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "${chat.otherUser?.getUserName() ?: "Chat"} — ${chat.lastMessage}",
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
    student: Student
) {
    val vm: StudentHomeViewModel = hiltViewModel()

    val myTutors by vm.myTutors
    val discoverTutors by vm.discoverTutors
    val chats by vm.latestChats

    val palette = LocalAppPalette.current
    val homeColors = palette.home
    val isDark = palette.isDark

    val backgroundBrush = Brush.verticalGradient(
        colors = if (isDark) AppGradients.Dark else AppGradients.Light
    )

    LaunchedEffect(student.user.getUID()) {
        vm.load(student)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentPadding = PaddingValues(16.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { MyTutorsSection(myTutors, homeColors) }
        item { DiscoverTutorsSection(discoverTutors, homeColors) }
        item { LatestChatsSection(chats, homeColors) }
    }
}
