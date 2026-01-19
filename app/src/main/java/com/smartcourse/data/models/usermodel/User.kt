package com.smartcourse.data.models.usermodel

import com.smartcourse.data.models.table.UserTable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// User.kt
@Serializable
data class User(
    @SerialName(UserTable.ID)
    val userId: String,
    @SerialName(UserTable.NAME)
    var name: String? = null,
    @SerialName(UserTable.EMAIL)
    var email: String? = null,
    @SerialName(UserTable.IMAGE)
    var image: String? = null,
    @SerialName(UserTable.ROLE)
    var role: UserRole? = null,
    @SerialName(UserTable.BIO)
    var bio: String? = null,

    // Optimization: Keep all specific data in one place
    // Use @Transient so Supabase doesn't try to find these columns in the user_table
    @Transient
    var courses: List<Course> = emptyList(),

    @Transient
    var favoritesCount: Int = 0,

    @Transient
    var savedUserIds: Set<String> = emptySet()
) {
    // Helper property to replace the old DomainUser functionality
    val displayName: String get() = name.orEmpty()
    val displayImage: String get() = image.orEmpty()



}