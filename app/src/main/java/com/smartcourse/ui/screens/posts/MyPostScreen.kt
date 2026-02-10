package com.smartcourse.ui.screens.posts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.data.models.usermodel.Post

@Composable
fun MyPostsScreen(navController: NavController) {
    val viewModel: MyPostViewModel = hiltViewModel()
    val user by viewModel.user.collectAsState()
    val posts by viewModel.myPosts.collectAsState()
    val courses by viewModel.userCourses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp) // Softer corners
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Post")
            }
        },
        containerColor = Color(0xFF121826)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
        ) {
            // Refined Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "My Management",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Active as: ${user?.displayName ?: "..."}",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (isLoading && posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50), strokeWidth = 3.dp)
                }
            } else if (posts.isEmpty()) {
                // Empty State Illustration/Text
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No posts yet.", color = Color.Gray, fontSize = 16.sp)
                        TextButton(onClick = { showAddDialog = true }) {
                            Text("Create your first post", color = Color(0xFF4CAF50))
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(posts, key = { it.id }) { post ->
                        PostManagementCard(
                            post = post, // Passing the full post object as refined previously
                            onDelete = { viewModel.deletePost(post.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CreatePostDialog(
            availableCourses = courses,
            onDismiss = { showAddDialog = false },
            onConfirm = { content, selectedIds ->
                viewModel.createPost(content, selectedIds)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PostManagementCard(
    post: Post, // Pass object instead of just content
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2A44)),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // Match the Feed Card shape
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = post.content,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFFF5252))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Added metadata row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                post.courses.forEach { course ->
                    Text(
                        text = "#${course.name} ",
                        color = Color(0xFF4CAF50),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(post.createdAt.substringBefore("T"), color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    availableCourses: List<com.smartcourse.data.models.usermodel.Course>,
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1F2A44),
        shape = RoundedCornerShape(20.dp),
        title = { Text("Create New Post", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= 250) text = it },
                    placeholder = { Text("What's on your mind?", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF4CAF50)
                    )
                )

                // Character Counter
                Text(
                    text = "${text.length}/250",
                    modifier = Modifier.align(Alignment.End),
                    color = if (text.length > 240) Color.Red else Color.Gray,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tag Courses:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                // Scrollable Course Selection
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(availableCourses) { course ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedIds.contains(course.id),
                                onCheckedChange = { checked ->
                                    if (checked) selectedIds.add(course.id) else selectedIds.remove(course.id)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                            )
                            Text(course.name, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text, selectedIds.toList()) },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Post", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}