package com.smartcourse.ui.screens.user.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.smartcourse.data.models.usermodel.Course

@Composable
fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Name") },
        text = {
            TextField(value = text, onValueChange = { text = it }, singleLine = true)
        },
        confirmButton = {
            TextButton(onClick = { onSave(text); onDismiss() }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditBioDialog(
    currentBio: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentBio ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Bio") },
        text = {
            TextField(value = text, onValueChange = { text = it }, modifier = Modifier.height(150.dp))
        },
        confirmButton = {
            TextButton(onClick = { onSave(text); onDismiss() }) { Text("Save") }
        }
    )
}

@Composable
fun EditCoursesDialog(
    allCourses: List<Course>,
    userCourses: List<Course>,
    onDismiss: () -> Unit,
    onAdd: (Course) -> Unit,
    onRemove: (Course) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1A1128) // Matching your theme
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Select Your Courses", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(allCourses) { course ->
                        val isSelected = userCourses.any { it.id == course.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { if (isSelected) onRemove(course) else onAdd(course) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { if (isSelected) onRemove(course) else onAdd(course) }
                            )
                            Text(course.name, color = Color.White, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Done")
                }
            }
        }
    }
}