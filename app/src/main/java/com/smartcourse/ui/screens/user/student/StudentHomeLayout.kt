package com.smartcourse.ui.screens.user.student

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.StudentHomeColorPalette


/* =========================================================
   DATA
   ========================================================= */

data class TutorUiData(val name: String, val subject: String, val rating: String)
data class ChatUiData(val name: String, val message: String)

/* =========================================================
   TEMP DATA (SWAP WITH VM)
   ========================================================= */

fun tempMyTutors() = listOf(
    TutorUiData("Dana", "Math", "4.9"),
    TutorUiData("Ron", "Physics", "4.6"),
    TutorUiData("Alex", "Chem", "4.8"),
    TutorUiData("Dana", "Math", "4.9"),
    TutorUiData("Ron", "Physics", "4.6"),
    TutorUiData("Alex", "Chem", "4.8")
)

fun tempDiscoverTutors() = listOf(
    TutorUiData("Dana", "Linear Algebra", "4.9"),
    TutorUiData("Alex", "Organic Chem", "4.8"),
    TutorUiData("Ron", "Quantum Physics", "4.6"),
    TutorUiData("Ben", "Biology", "4.7"),
)

fun tempLatestChats() = listOf(
    ChatUiData("Dana", "Tomorrow works"),
    ChatUiData("Ron", "Sent the exercises"),
    ChatUiData("Alex", "See you at 5"),
)

/* =========================================================
   BASIC COMPONENTS
   ========================================================= */

@Composable
fun TutorAvatar(
    tutor: TutorUiData,
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
            tutor.name,
            color = colors.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(tutor.subject, color = colors.subtext, fontSize = 14.sp)
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
    tutor: TutorUiData,
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
                            tutor.name,
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(tutor.subject, color = colors.subtext, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("★ ${tutor.rating}", color = colors.star, fontSize = 12.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton("Chat", true, colors) {
                    // todo option go open chat / create chat
                    Log.d("Chat", "Cicked Chat ${tutor.name}")
                }
                ActionButton("Save", false, colors) {
                    // todo option to save user to list
                    Log.d("Save", "Cicked Save ${tutor.name}")
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
    tutors: List<TutorUiData>,
    colors: StudentHomeColorPalette
) {
    Text("My Tutors", color = colors.textPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(10.dp))


    LazyRow(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
        items(tutors.size) {
            TutorAvatar(tutor = tutors[it], colors = colors ){}
        }
    }
}

@Composable
fun DiscoverTutorsSection(
    tutors: List<TutorUiData>,
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
        items(tutors.takeLast(4)) { tutor ->
            TutorDiscoverCard(tutor = tutor, colors = colors) {
                // Handle navigation to profile
                Log.d("Click on card", "Go to ${tutor.name} profile")
            }
        }
    }
}

@Composable
fun LatestChatsSection(
    chats: List<ChatUiData>,
    colors: StudentHomeColorPalette
) {
    Text(
        "Latest Chats",
        color = colors.textPrimary,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    chats.takeLast(3).forEach {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.card)
                .padding(horizontal = 16.dp)
                .clickable {
                    //todo go to chat
                    Log.d("Load Last Chat", "$it")
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
            //todo need to change later to load the lastest msg
            Text("${it.name} — ${it.message}", color = colors.textPrimary, fontSize = 15.sp)
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
    student: Student,
) {
    val studentHomeViewModel: StudentHomeViewModel = hiltViewModel()

    // 1. Pull the centralized palette and gradients
    val palette = LocalAppPalette.current
    val homeColors = palette.home
    val isDark = palette.isDark

    // 2. Use the centralized gradients (Matches Settings and Login)
    val backgroundBrush = Brush.verticalGradient(
        colors = if (isDark) AppGradients.Dark else AppGradients.Light
    )

    LaunchedEffect(student.user.getUID()) {
        studentHomeViewModel.load(student)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentPadding = PaddingValues(16.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 3. Pass the clean homeColors palette to your sections
        item { MyTutorsSection(tempMyTutors(), homeColors) }
        item { DiscoverTutorsSection(tempDiscoverTutors(), homeColors) }
        item { LatestChatsSection(tempLatestChats(), homeColors) }
    }
}

