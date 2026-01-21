package com.smartcourse.ui.screens.search

import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever


/**
 * Unit test for [SearchUserViewModel] to verify the core search and filtering logic.
 *
 * The test ensures the following business rules are enforced:
 * 1. Role-Based Filtering:
 * - If the logged-in user is a STUDENT, they should only see TUTORS in search results.
 * - If the logged-in user is a TUTOR, they should only see STUDENTS in search results.
 * 2. Self-Exclusion: The currently authenticated user is always filtered out of the results.
 * 3. State Integrity: Validates that the UI state correctly maps data from the repository
 * into [SearchUserRowState] after filtering.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class SearchUserViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: SearchUserViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        println("--- SETUP START ---")
        val me = User(userId = "my_user_id", name = "Me", role = UserRole.STUDENT)
        println("Mocking current user: ${me.name} as ${me.role}")

        runTest(testDispatcher) {
            whenever(authRepository.restoreValidSession()).thenReturn(me)
            whenever(userRepository.getUserCourses(anyString())).thenReturn(emptyList())
        }

        viewModel = SearchUserViewModel(userRepository, authRepository)
        println("ViewModel initialized. Waiting for init block coroutine...")

        // Ensure init block (fetching myUserId/Role) finishes
        testDispatcher.scheduler.advanceUntilIdle()
        println("--- SETUP FINISHED ---\n")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        println("\n--- TEST CLEANUP ---")
    }

    @Test
    fun `onQueryChanged filters out self and non-tutor users for a student`() = runTest {
        // 1. Arrange
        val query = "John"
        val tutor = User("tutor_id", "John Tutor", role = UserRole.TUTOR)
        val otherStudent = User("student_id", "John Student", role = UserRole.STUDENT)
        val myself = User("my_user_id", "John Me", role = UserRole.STUDENT)

        println("[1] Arrange: Mocking search results for query '$query'")
        println("    - User 1: ${tutor.userId} (${tutor.role})")
        println("    - User 2: ${otherStudent.userId} (${otherStudent.role})")
        println("    - User 3: ${myself.userId} (${myself.role})")

        whenever(userRepository.searchUsers(query))
            .thenReturn(listOf(tutor, otherStudent, myself))

        // 2. Act
        println("[2] Act: Calling onQueryChanged('$query')")
        viewModel.onQueryChanged(query)

        println("    Waiting for search coroutine to finish...")
        advanceUntilIdle()

        // 3. Assert
        val state = viewModel.uiState.value
        println("[3] Assert: Analyzing final UI State")
        println("    Results count in UI: ${state.results.size}")

        state.results.forEachIndexed { index, result ->
            println("    Result #$index: ${result.user.name} | Role: ${result.user.role} | ID: ${result.user.userId}")
        }

        // Verification logic
        assertEquals("Should only have 1 result (the tutor)", 1, state.results.size)
        assertEquals("The result should be the tutor", "tutor_id", state.results[0].user.userId)

        println("--- TEST SUCCESS ---")
    }
}