package com.smartcourse.data.models.table


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFavoriteRow(
    @SerialName("user_a") val userA: String,
    @SerialName("user_b") val userB: String
)

object UserFavoriteTable {
    const val TABLE = "user_favorite"
    const val USER_A = "user_a"
    const val USER_B = "user_b"
}

