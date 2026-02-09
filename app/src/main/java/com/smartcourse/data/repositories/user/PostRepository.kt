package com.smartcourse.data.repositories.user

import com.smartcourse.data.models.table.TableNames
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.Post
import com.smartcourse.data.models.usermodel.PostWithCourseRow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val client: SupabaseClient
) {

    suspend fun loadPosts(): List<Post> {
        val rows = client
            .from(TableNames.POSTS_WITH_COURSES)
            .select()
            .decodeList<PostWithCourseRow>()

        return rows
            .groupBy { it.post_id }
            .map { (_, group) ->
                Post(
                    id = group.first().post_id,
                    userId = group.first().user_id,
                    content = group.first().content,
                    createdAt = group.first().created_at,
                    courses = group.map {
                        Course(
                            id = it.course_id,
                            name = it.course_name
                        )
                    }
                )
            }
    }

    suspend fun createPost(
        content: String,
        courseIds: List<String>
    ) {
        // 1 Insert post
        val postId = client
            .from(TableNames.POSTS)
            .insert(mapOf("content" to content)) {
                select()
            }
            .decodeSingle<Map<String, String>>()["id"]!!

        // 2 Insert course links
        val links = courseIds.map {
            mapOf(
                "post_id" to postId,
                "course_id" to it
            )
        }

        client
            .from(TableNames.POSTS_COURSES)
            .insert(links)
    }
}
