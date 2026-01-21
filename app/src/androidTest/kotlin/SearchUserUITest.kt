import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.UserRepository
import com.smartcourse.ui.screens.search.SearchUserScreen
import com.smartcourse.ui.screens.search.SearchUserViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class SearchUserUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val authRepository = mockk<AuthRepository>(relaxed = true)

    /**
     * Test 1: Multiple Results Flow (Matches Manual Scenario 1)
     * Verifies that searching for "John" displays all returned tutors.
     */
    @Test
    fun testMultipleResultsSearchFlow() {
        val me = User(userId = "me", name = "Me", role = UserRole.STUDENT)
        val tutor1 = User("t1", "John Smith", role = UserRole.TUTOR)
        val tutor2 = User("t2", "John Doe", role = UserRole.TUTOR)

        coEvery { authRepository.restoreValidSession() } returns me
        coEvery { userRepository.searchUsers("John") } returns listOf(tutor1, tutor2)
        coEvery { userRepository.getUserCourses(any()) } returns emptyList()

        val viewModel = SearchUserViewModel(userRepository, authRepository)

        composeTestRule.setContent {
            val navController = rememberNavController()
            SearchUserScreen(navController = navController, viewModel = viewModel)
        }

        // Action: Type "John"
        composeTestRule.onNode(hasText("Search by name") and hasSetTextAction())
            .performTextInput("John")

        Thread.sleep(2000)

        // Verification: Both results should be displayed
        composeTestRule.onNodeWithText("John Smith").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()

        Thread.sleep(3000)
    }

    /**
     * Test 2: Empty Results Flow (Matches Manual Scenario 2)
     * Verifies that the UI remains empty when no users are found.
     */
    @Test
    fun testEmptyResultsSearchFlow() {
        val me = User(userId = "me", name = "Me", role = UserRole.STUDENT)
        coEvery { authRepository.restoreValidSession() } returns me
        coEvery { userRepository.searchUsers("UnknownName") } returns emptyList()

        val viewModel = SearchUserViewModel(userRepository, authRepository)

        composeTestRule.setContent {
            val navController = rememberNavController()
            SearchUserScreen(navController = navController, viewModel = viewModel)
        }

        // Action: Type name that doesn't exist
        composeTestRule.onNode(hasText("Search by name") and hasSetTextAction())
            .performTextInput("UnknownName")

        Thread.sleep(2000)

        // Verification: Ensure common names are NOT displayed
        composeTestRule.onNodeWithText("John Smith").assertDoesNotExist()
    }

    /**
     * Test 3: Specific Match & Exclusion (Matches Manual Scenario 3)
     * Verifies search accuracy and exclusion of non-returned similar names.
     */
    @Test
    fun testSpecificStringMatchFlow() {
        val me = User(userId = "me", name = "Me", role = UserRole.STUDENT)
        val orian = User("u1", "Orian", role = UserRole.TUTOR)
        // We don't need to define 'or' here because the mock returns only 'orian'

        coEvery { authRepository.restoreValidSession() } returns me
        // The mock explicitly returns only "Orian" for the query "Ori"
        coEvery { userRepository.searchUsers("Ori") } returns listOf(orian)
        coEvery { userRepository.getUserCourses(any()) } returns emptyList()

        val viewModel = SearchUserViewModel(userRepository, authRepository)

        composeTestRule.setContent {
            val navController = rememberNavController()
            SearchUserScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Action: Type "Ori" into the search field
        composeTestRule.onNode(
            (hasText("Search by name", ignoreCase = true)
                    or hasAnyChild(hasText("Search by name")))
                    and hasSetTextAction()
        ).performTextInput("Ori")

        Thread.sleep(2000) // Observe the typing

        // Verification: "Orian" MUST be visible in the results
        composeTestRule.onNodeWithText("Orian").assertIsDisplayed()

        // Verification: "Or" MUST NOT be displayed
        composeTestRule.onNodeWithText("Or").assertDoesNotExist()

        Thread.sleep(3000) // Observe the final state
    }
}