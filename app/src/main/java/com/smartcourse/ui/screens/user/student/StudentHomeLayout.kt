package com.smartcourse.ui.screens.user.student

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.ui.theme.screens.StudentHomeColorPalette
import com.smartcourse.ui.theme.screens.StudentHomeLayoutColors

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
    TutorUiData("Alex", "Chem", "4.8")
)

fun tempDiscoverTutors() = listOf(
    TutorUiData("Dana", "Linear Algebra", "4.9"),
    TutorUiData("Alex", "Organic Chem", "4.8"),
    TutorUiData("Ron", "Quantum Physics", "4.6"),
    TutorUiData("Ben", "Biology", "4.7")
)

fun tempLatestChats() = listOf(
    ChatUiData("Dana", "Tomorrow works"),
    ChatUiData("Ron", "Sent the exercises"),
    ChatUiData("Alex", "See you at 5")
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
        Text(tutor.name, color = colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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
                        Text(tutor.name, color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(tutor.subject, color = colors.subtext, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("★ ${tutor.rating}", color = colors.star, fontSize = 12.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton("Chat", true, colors) {}
                ActionButton("Save", false, colors) {}
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

    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(tutors.size) { TutorAvatar(tutors[it], colors) {} }
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
        items(tutors) { TutorDiscoverCard(it, colors) {} }
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

    chats.forEach {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.card)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.accent)
            )
            Spacer(modifier = Modifier.width(10.dp))
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
    authVM: AuthViewModel
) {
    val student = authVM.domainUser as? Student ?: return

    val colors = if (isSystemInDarkTheme()) {
        StudentHomeLayoutColors.Dark
    } else {
        StudentHomeLayoutColors.Light
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(16.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { MyTutorsSection(tempMyTutors(), colors) }
        item { DiscoverTutorsSection(tempDiscoverTutors(), colors) }
        item { LatestChatsSection(tempLatestChats(), colors) }
    }
}
