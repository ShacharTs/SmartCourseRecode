package com.smartcourse.ui.screens.chat

import com.smartcourse.data.models.usermodel.User

fun resolveUsers(myId: String, users: List<User?>): Pair<User?, User?> {
    val u1 = users.getOrNull(0)
    val u2 = users.getOrNull(1)
    val me = if (u1?.userId == myId) u1 else u2
    val other = if (u1?.userId == myId) u2 else u1
    return me to other
}
