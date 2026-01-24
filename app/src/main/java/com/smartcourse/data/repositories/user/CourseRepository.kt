package com.smartcourse.data.repositories.user

import com.smartcourse.data.models.table.TableNames
import com.smartcourse.data.models.table.UserCourseTable
import com.smartcourse.data.models.table.UserTable
import com.smartcourse.data.models.usermodel.Course
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val client: SupabaseClient
) {
    suspend fun getAllCourses(): List<Course> = client.from(TableNames.COURSE_LIST).select().decodeList()

    suspend fun getCourseById(courseId: String): Course? {
        return client.postgrest[TableNames.COURSE_LIST]
            .select {
                filter { eq("id", courseId) }
                limit(1)
            }
            .decodeSingleOrNull<Course>()
    }

    suspend fun getUserCourses(userId: String): List<UserCourseTable> {
        return client.postgrest[TableNames.USER_COURSES]
            .select { filter { eq(UserTable.ID, userId) } }
            .decodeList<UserCourseTable>()
    }

    suspend fun addUserCourse(userId: String, courseId: String) {
        client.from(TableNames.USER_COURSES).upsert(
            value = mapOf("user_id" to userId, "course_id" to courseId),
            onConflict = "user_id,course_id"
        )
    }

    suspend fun removeUserCourse(userId: String, courseId: String) {
        client.from(TableNames.USER_COURSES).delete {
            filter {
                eq(UserTable.ID, userId)
                eq("course_id", courseId)
            }
        }
    }
}