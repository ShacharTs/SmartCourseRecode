package com.smartcourse.ui.screens.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
                contentColor = Color.White
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
            // Personalized Header
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "My Posts",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Logged in as: ${user?.displayName ?: "Loading..."}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            if (isLoading && posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts, key = { it.id }) { post ->
                        PostManagementCard(
                            content = post.content,
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
fun PostManagementCard(content: String, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2A44)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(content, color = Color.White, modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
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
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = { Text("Create New Post") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Content", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Tag Courses:", style = MaterialTheme.typography.labelMedium, color = Color.White)
                availableCourses.forEach { course ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedIds.contains(course.id),
                            onCheckedChange = { checked ->
                                if (checked) selectedIds.add(course.id) else selectedIds.remove(course.id)
                            }
                        )
                        Text(course.name, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text, selectedIds.toList()) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}