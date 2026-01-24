package com.smartcourse.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    text: String,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    icon: Painter? = null,
    iconSize: Dp = 20.dp,
    spacing: Dp = 8.dp,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit
) {
    val sized = modifier
        .let { if (width != null) it.widthIn(min = width) else it }
        .let { if (height != null) it.heightIn(min = height) else it }

    val colors = ButtonDefaults.buttonColors(
        containerColor = backgroundColor ?: MaterialTheme.colorScheme.primary,
        contentColor = contentColor ?: MaterialTheme.colorScheme.onPrimary
    )

    Button(
        onClick = onClick,
        modifier = sized,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(spacing))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
