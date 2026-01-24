package com.smartcourse.ui.screens.chat.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AttachSheetContent(
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onLocation: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AttachRow("Camera", Icons.Default.CameraAlt, onCamera)
        AttachRow("Gallery", Icons.Default.Image, onGallery)
        AttachRow("Use Location", Icons.Default.LocationOn, onLocation)
    }
}

@Composable
private fun AttachRow(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null)
            Spacer(Modifier.width(16.dp))
            Text(text)
        }
    }
}
