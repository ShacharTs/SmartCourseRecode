package com.smartcourse.data.models.usermodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
enum class UserRole {

    @SerialName("student")
    STUDENT,

    @SerialName("tutor")
    TUTOR,

    @SerialName("admin")
    ADMIN,


    @SerialName("temp")
    TEMP
}

fun UserRole.serialName(): String {
    return this::class.java.getField(this.name).getAnnotation(SerialName::class.java).value
}





