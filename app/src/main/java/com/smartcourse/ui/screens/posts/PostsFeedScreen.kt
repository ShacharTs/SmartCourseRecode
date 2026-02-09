package com.smartcourse.ui.screens.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.navigation.Screen


data class PostUiModel(
    val id: String,
    val authorRole: UserRole,
    val authorTitle: String,
    val description: String,
    val courses: List<String>
)

@Composable
fun PostsFeedScreen(
    navController: NavController,
) {
    val posts = getDemoPosts()

    val auth : AuthViewModel = hiltViewModel()

    //todo temp for now
     val myId: String =
        requireNotNull(auth.currentUser.value?.userId) {
            "test created without logged-in user"
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(Color(0xFF121826))
    ) {
        TopBar(
            onMyPostClick = {
                navController.navigate(Screen.ShowPost.createRoute(userId = myId))
            }
        )

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(posts) { post ->
                PostCard(
                    post = post,
                    onClick = {
                        navController.navigate("post/${post.id}")
                    }
                )
            }
        }
    }
}

private fun getDemoPosts(): List<PostUiModel> {
    return listOf(
        PostUiModel(
            id = "1",
            authorRole = UserRole.STUDENT,
            authorTitle = "Student • Algorithms",
            description = "Looking for a tutor to help prepare for the final exam.",
            courses = listOf("Algorithms", "Java")
        ),
        PostUiModel(
            id = "2",
            authorRole = UserRole.TUTOR,
            authorTitle = "Tutor • Python",
            description = "CS tutor offering online lessons, flexible schedule.",
            courses = listOf("Python", "Data Structures")
        )
    )
}



@Composable
private fun TopBar(
    onMyPostClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .background(Color(0xFF1C2333))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Posts",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onMyPostClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = "My Post",
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PostCard(
    post: PostUiModel,
    onClick: () -> Unit
) {
    val accentColor = when (post.authorRole) {
        UserRole.STUDENT -> Color(0xFF4CAF50)
        UserRole.TUTOR -> Color(0xFFFFC107)
        else -> Color(0xFF9E9E9E)
    }

    val tagColor = when (post.authorRole) {
        UserRole.STUDENT -> Color(0xFF2E7DFF)
        UserRole.TUTOR -> Color(0xFFFF9800)
        else -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2A44)
        ),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = post.authorTitle,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Course tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                post.courses.forEach { course ->
                    CourseTag(text = course, color = tagColor)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = post.description,
                color = Color(0xFFD0D4E0),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "View",
                        fontSize = 12.sp,
                        color = if (post.authorRole == UserRole.TUTOR)
                            Color(0xFF1E1E1E) else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseTag(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
