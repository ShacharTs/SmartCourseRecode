package com.smartcourse.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


@Composable
fun CustomBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
    ) {
    val clipped = modifier.clip(RoundedCornerShape(20.dp))

    val clickable = if (onClick != null) {
        clipped.clickable { onClick() }
    } else clipped

    Box(
        modifier = clickable,
        contentAlignment = contentAlignment,
        content = { content() }
    )
}
