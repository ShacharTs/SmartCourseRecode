package com.smartcourse.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun SearchUserScreen(
    navController: NavController,
    viewModel: SearchUserViewModel = hiltViewModel()
) {
    val colors = LocalAppPalette.current.search
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundGradient.first())
            .padding(horizontal = 16.dp)
            .statusBarsPadding()
    ) {
        Spacer(Modifier.height(12.dp))
        Text("Search", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
        Spacer(Modifier.height(12.dp))

        TextField(
            value = state.query,
            onValueChange = viewModel::onQueryChanged,
            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(14.dp)),
            placeholder = { Text("Search by name", fontSize = 14.sp, color = colors.subtext) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.searchField,
                unfocusedContainerColor = colors.searchField,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = colors.tagAccent,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary
            )
        )

        Spacer(Modifier.height(12.dp))

        // Horizontal Filter Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = state.selectedCourse == null,
                    onClick = { viewModel.onCourseFilterChanged(null) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.tagAccent, // This makes it pink when selected
                        selectedLabelColor = Color.White,
                        labelColor = colors.textPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = colors.subtext,
                        enabled = true,
                        selected = state.selectedCourse == null
                    )
                )
            }
            items(state.availableCourses) { course ->
                FilterChip(
                    selected = state.selectedCourse == course,
                    onClick = {
                        val nextValue = if (state.selectedCourse == course) null else course
                        viewModel.onCourseFilterChanged(nextValue)
                    },
                    label = { Text(course) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.tagAccent,
                        selectedLabelColor = Color.White,
                        labelColor = colors.textPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = colors.subtext,
                        enabled = true,
                        selected = state.selectedCourse == course
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.tagAccent)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.results) { row ->
                    SearchUserRow(
                        row = row,
                        onClick = { navController.navigate(Screen.ShowOtherProfile.createRoute(row.user.userId)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchUserRow(
    row: SearchUserRowState,
    onClick: () -> Unit
) {
    val colors = LocalAppPalette.current.search
    val user = row.user
    val courses = row.courses
    val avatarUrl = user.image

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.card)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.avatarBackground),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = user.displayName
                        .split(" ")
                        .take(2)
                        .joinToString("") { it.first().uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = user.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )

            if (courses.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = courses.joinToString(" · "),
                    fontSize = 12.sp,
                    color = colors.tagAccent,
                    maxLines = 2
                )
            }
        }
    }
}