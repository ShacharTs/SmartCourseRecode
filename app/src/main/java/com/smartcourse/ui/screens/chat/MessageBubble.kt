package com.smartcourse.ui.screens.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun MessageBubble(
    isMine: Boolean,
    text: String?,
) {
    if (text.isNullOrBlank()) return

    val chat = LocalAppPalette.current.chatRoom
    val bg = if (isMine) chat.outgoingBubble else chat.incomingBubble

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isMine) 96.dp else 12.dp,
                end = if (isMine) 12.dp else 96.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
        horizontalArrangement =
            if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bg,
            shape = RoundedCornerShape(18.dp),
            border = if (!isMine)
                BorderStroke(1.dp, chat.incomingBorder)
            else null
        ) {
            CustomText(
                text = text.trim(),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
