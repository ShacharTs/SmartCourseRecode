package com.smartcourse.data.models.usermodel

import com.smartcourse.data.models.table.UserTable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    @SerialName(UserTable.ID)
    val userId: String,

    @SerialName(UserTable.NAME)
    var name: String? = null, // No 'override' here

    @SerialName(UserTable.EMAIL)
    var email: String? = null, // No 'override' here

    @SerialName(UserTable.IMAGE)
    var image: String? = null,

    @SerialName(UserTable.ROLE)
    var role: UserRole? = null,

    @SerialName(UserTable.BIO)
    var bio: String? = null,

    @Transient
    var courses: List<Course> = emptyList()

) : DomainUser {
    override val user: User get() = this

    // Keeping your original getters exactly as they were
    fun getUID() : String = userId
    fun getUserName() : String = name ?: ""
    fun getUserEmail() : String = email ?: ""
    fun setUserName(name : String) { this@User.name = name }
}