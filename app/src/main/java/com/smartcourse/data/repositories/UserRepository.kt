package com.smartcourse.data.repositories

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    fun testRepo(): String {
        return "UserRepository is OK"
    }
}
