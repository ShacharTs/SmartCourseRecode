package com.smartcourse.ui.screens.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

    // State for the Pop-out (Bottom Sheet)
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    val myId = currentUser?.userId

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121826))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                onMyPostClick = {
                    myId?.let { navController.navigate(Screen.MyPosts.createRoute(userId = it)) }
                }
            )

            if (isLoading && posts.isEmpty()) {
                LoadingState()
            } else {
                PostsList(
                    posts = posts,
                    onPostClick = { post -> selectedPost = post }
                )
            }
        }

        // Pop-out Dialog logic
        selectedPost?.let { post ->
            PostDetailDialog(
                post = post,
                onDismiss = { selectedPost = null }
            )
        }
    }
}



@Composable
private fun PostsList(
    posts: List<Post>,
    onPostClick: (Post) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = posts,
            key = { it.id }
        ) { post ->
            PostRow(
                post = post,
                onClick = { onPostClick(post) }
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF4CAF50))
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2A44)),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostHeader(
                    displayName = post.displayName,
                    imageUrl = post.imageUrl,
                    role = role,
                    accentColor = accentColor
                )
                // Removes the messy ISO time zones
                Text(
                    text = post.createdAt.substringBefore("T"),
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = post.content,
                color = Color(0xFFE0E0E0),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 4
            )

            Spacer(Modifier.height(16.dp))

            PostFooter(post = post, accentColor = accentColor)
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
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(32.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(accentColor))
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                text = if (role == UserRole.TUTOR) "Tutor" else "Student",
                color = accentColor,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PostFooter(post: Post, accentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            post.courses.take(2).forEach { course ->
                CourseTag(text = course.name, color = accentColor.copy(alpha = 0.15f))
            }
        }
        Text("View Details →", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
    UserRole.STUDENT -> Color(0xFF4CAF50) // Green
    UserRole.TUTOR -> Color(0xFFFFC107)   // Amber/Yellow
    else -> Color.Gray
}

private fun getRoleTagColor(role: UserRole): Color = when (role) {
    UserRole.STUDENT -> Color(0xFF2E7DFF) // Blue tags for students
    UserRole.TUTOR -> Color(0xFFFF9800)   // Orange tags for tutors
    else -> Color.Gray
}


