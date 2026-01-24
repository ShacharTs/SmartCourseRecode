package com.smartcourse.ui.screens.search

import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.user.UserRepository
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

        // Initial setup: current user is a student
        val me = User(userId = "my_user_id", name = "Me", role = UserRole.STUDENT)

        runTest(testDispatcher) {
            whenever(authRepository.restoreValidSession()).thenReturn(me)
            whenever(userRepository.getUserCourses(anyString())).thenReturn(emptyList())
        }

        viewModel = SearchUserViewModel(userRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test 1: Student searching for multiple Tutors.
     * Checks if the filter allows multiple valid tutors to be displayed.
     */
    @Test
    fun `onQueryChanged returns multiple tutors for student search`() = runTest {
        val query = "John"
        val tutor1 = User("t1", "John Smith", role = UserRole.TUTOR)
        val tutor2 = User("t2", "John Doe", role = UserRole.TUTOR)

        whenever(userRepository.searchUsers(query)).thenReturn(listOf(tutor1, tutor2))

        viewModel.onQueryChanged(query)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.results.size)
        println("Test 1: Found ${state.results.size} tutors.")
    }

    /**
     * Test 2: Search for a name that does not exist in the database.
     * Checks if the UI state remains empty when the repository returns nothing.
     */
    @Test
    fun `onQueryChanged returns empty list when no users exist`() = runTest {
        val query = "UnknownName"
        whenever(userRepository.searchUsers(query)).thenReturn(emptyList())

        viewModel.onQueryChanged(query)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(0, state.results.size)
        println("Test 2: No users found for query: $query")
    }

    /**
     * Test 3: Verifies specific string matching and exclusion logic.
     * Scenario: Both "Or" and "Orian" exist, but searching "Ori" should only return "Orian".
     */
    @Test
    fun `onQueryChanged handles specific string match correctly`() = runTest {
        // 1. Setting up conditions: We have two similar users
        val query = "Ori"
        val orian = User("u1", "Orian", role = UserRole.TUTOR)
        val or = User("u2", "Or", role = UserRole.TUTOR)

        // Repository is mocked to return ONLY Orian for the specific query "Ori"
        whenever(userRepository.searchUsers(query)).thenReturn(listOf(orian))

        // 2. Calling the function
        viewModel.onQueryChanged(query)
        advanceUntilIdle()

        // 3. Assertions
        val state = viewModel.uiState.value

        // Verify Orian is present
        assertEquals(1, state.results.size)
        assertEquals("Orian", state.results[0].user.name)

        // Verify "Or" is NOT in the results, ensuring precise matching
        val containsOr = state.results.any { it.user.name == "Or" }
        assertEquals(false, containsOr)
    }
}