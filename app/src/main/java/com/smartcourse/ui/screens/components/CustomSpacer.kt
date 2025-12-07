package com.smartcourse.ui.screens.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomSpacer(
    height: Int? = null,
    width: Int? = null,
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier.then(
            when {
                height != null && width != null ->
                    Modifier.size(width.dp, height.dp)

                height != null ->
                    Modifier.height(height.dp)

                width != null ->
                    Modifier.width(width.dp)

                else ->
                    Modifier
            }
        )
    )
}

