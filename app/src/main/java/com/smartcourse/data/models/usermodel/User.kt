package com.smartcourse.data.models.usermodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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

    // later remove not needed anymore
    @Transient
    var courses: List<Course> = emptyList()


){

    fun getUID() : String {
        return userId
    }
    fun getUserName() : String {
        return name ?: ""
    }
    fun getUserEmail() : String {
        return email ?: ""
    }

    fun setUserName(name : String) {
        this@User.name = name
    }

    fun getUserRole() : UserRole? {
        return role
    }





}