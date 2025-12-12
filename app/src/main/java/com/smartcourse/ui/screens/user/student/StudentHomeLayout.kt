package com.smartcourse.ui.screens.user.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.repositories.AuthRepository


val BackgroundColor = Color(0xFF0E0E11) // #0E0E11
val CardColor = Color(0xFF1A1A22)       // #1A1A22
val PlaceholderColor = Color(0xFF2A2A32) // #2A2A32 (Used for Avatars and Buttons)
val SubtextColor = Color(0xFFB0B0B8)    // #B0B0B8
val StarYellow = Color(0xFFFFD54F)      // #FFD54F

/**
 * A circular placeholder for a tutor's avatar/profile picture.
 */
@Composable
fun TutorAvatar(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PlaceholderColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Name (text x="60" y="140")
        Text(
            text = name,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

/**
 * A reusable button for the action row in the card.
 */
@Composable
fun ActionButton(text: String) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PlaceholderColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp // Matched to SVG
        )
    }
}

/**
 * A reusable card for displaying a featured tutor.
 */
@Composable
fun TutorDiscoverCard(tutorName: String, subject: String, rating: String, availability: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp) // height="150"
            .clip(RoundedCornerShape(16.dp)) // rx="16" ry="16"
            .background(CardColor)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left: Avatar (circle cx="56" cy="255" r="24" -> diameter 48dp)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PlaceholderColor)
                    .align(Alignment.Top) // Align top to match text starting position
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Right: Details and Buttons
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Details
                Column {
                    Text(
                        text = tutorName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subject,
                        color = SubtextColor,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "★ $rating · $availability",
                        color = StarYellow,
                        fontSize = 14.sp
                    )
                }

                // Buttons (rect y="310")
                Row {
                    ActionButton(text = "Chat")
                    Spacer(modifier = Modifier.width(10.dp)) // Adjusted space between buttons
                    ActionButton(text = "Save")
                }
            }
        }
    }
}


// --- Main Composable ---
@Composable
fun StudentHomeLayout(
    navController: NavController,
    authVM: AuthViewModel
) {
    // Hardcoded Data (as per the SVG text)
    val myTutors = listOf("Dana", "Ron", "Alex")
    val latestChats = listOf(
        "Dana — Tomorrow works",
        "Ron — Sent the exercises"
    )

    // Using LazyColumn for vertical scrolling, similar to the 780px height
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp)
    ) {
        // --- 1. My Tutors Title ---
        item {
            // y="40"
            Text(
                text = "My Tutors",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp) // Adjusted for visual spacing
            )
        }

        // --- 2. Tutor Avatars ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                myTutors.forEach { name ->
                    TutorAvatar(name = name)
                }
            }
        }

        // --- 3. Discover Tutors Title ---
        item {
            // y="190"
            Text(
                text = "Discover Tutors",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        // --- 4. Tutor Card (Discovery) ---
        item {
            TutorDiscoverCard(
                tutorName = "Dana",
                subject = "Linear Algebra",
                rating = "4.9",
                availability = "Available today"
            )
            Spacer(modifier = Modifier.height(55.dp))
        }

        // --- 5. Latest Chats Title ---
        item {
            // y="420"
            Text(
                text = "Latest Chats",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // --- 6. Latest Chats List ---
        items(latestChats) { chat ->
            Text(
                text = chat,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 15.dp) // Adjusted to match the y-spacing
            )
        }

        // Final bottom padding
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}