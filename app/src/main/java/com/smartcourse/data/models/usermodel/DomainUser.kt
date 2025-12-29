package com.smartcourse.data.models.usermodel


interface DomainUser {
    val user: User

    val id: String get() = user.userId
    val displayName: String get() = user.name.orEmpty()
    val displayEmail: String get() = user.email.orEmpty()
    val displayImage: String get() = user.image.orEmpty()
    val userRole: UserRole? get() = user.role
}