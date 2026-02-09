package com.smartcourse.ui.screens.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.Post
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.navigation.Screen

@Composable
fun PostsFeedScreen(
    navController: NavController
) {
    val feedViewModel: PostFeedViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    val posts by feedViewModel.posts.collectAsState()
    val isLoading by feedViewModel.isLoading.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val myId = currentUser?.userId



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(Color(0xFF121826))
    ) {

        TopBar(
            onMyPostClick = {
                myId?.let {
                    navController.navigate(
                        Screen.ShowPost.createRoute(userId = it)
                    )
                }
            }
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading posts...", color = Color.White)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = posts,
                    key = { it.id }
                ) { post ->
                    PostRow(
                        post = post,
                        onClick = {
                            navController.navigate("post/${post.id}")
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun TopBar(
    onMyPostClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
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
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 6.dp
            )
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
fun PostRow(
    post: Post,
    onClick: () -> Unit
) {
    val role = post.userRole
    val accentColor = getRoleAccentColor(role)
    val tagColor = getRoleTagColor(role)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2A44)),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Profile Image & Name
            PostHeader(
                displayName = post.displayName,
                imageUrl = post.imageUrl,
                role = role,
                accentColor = accentColor
            )

            Spacer(Modifier.height(12.dp))

            // Tags: Enrolled Courses
            PostCourseTags(
                courses = post.courses,
                tagColor = tagColor
            )

            Spacer(Modifier.height(12.dp))

            // Body: Post Content
            PostContent(content = post.content)

            Spacer(Modifier.height(12.dp))

            // Footer: Action Button
            PostFooter(
                accentColor = accentColor,
                onClick = onClick
            )
        }
    }
}


@Composable
private fun PostHeader(
    displayName: String,
    imageUrl: String?,
    role: UserRole,
    accentColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Profile Image or Fallback Circle
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = if (role == UserRole.TUTOR) "Tutor: $displayName" else "Student: $displayName",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun PostCourseTags(
    courses: List<Course>,
    tagColor: Color
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        courses.forEach { course ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(tagColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(text = course.name, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PostContent(content: String) {
    Text(
        text = content,
        color = Color(0xFFD0D4E0),
        fontSize = 12.sp
    )
}

@Composable
private fun PostFooter(
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Text("View", fontSize = 12.sp, color = Color.White)
        }
    }
}

@Composable
fun CourseTag(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}

private fun getRoleAccentColor(role: UserRole): Color = when (role) {
    UserRole.STUDENT -> Color(0xFF4CAF50)
    UserRole.TUTOR -> Color(0xFFFFC107)
    else -> Color(0xFF9E9E9E)
}

private fun getRoleTagColor(role: UserRole): Color = when (role) {
    UserRole.STUDENT -> Color(0xFF2E7DFF)
    UserRole.TUTOR -> Color(0xFFFF9800)
    else -> Color(0xFF757575)
}


