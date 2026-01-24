import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.user.UserRepository
import com.smartcourse.ui.screens.search.SearchUserScreen
import com.smartcourse.ui.screens.search.SearchUserViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class SearchUserUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val me = User(userId = "me", name = "Me", role = UserRole.STUDENT)

    @Before
    fun setup() {
        coEvery { authRepository.restoreValidSession() } returns me
        coEvery { userRepository.getUserCourses(any()) } returns emptyList()
    }

    private fun launchSearchScreen(query: String, results: List<User>, expectedResultName: String? = null) {
        coEvery { userRepository.searchUsers(query) } returns results

        val viewModel = SearchUserViewModel(userRepository, authRepository)
        composeTestRule.setContent {
            SearchUserScreen(navController = rememberNavController(), viewModel = viewModel)
        }

        // Action: Type the search query
        composeTestRule.onNode(hasSetTextAction()).performTextInput(query)

        // Fix: If we expect a result, wait until it is actually displayed (handles debounce/async)
        if (expectedResultName != null) {
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText(expectedResultName)
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }
    }

    @Test
    fun testMultipleResultsSearchFlow() {
        val tutors = listOf(
            User("t1", "John Smith", role = UserRole.TUTOR),
            User("t2", "John Doe", role = UserRole.TUTOR)
        )
        // We pass "John Smith" so the test waits for it to appear
        launchSearchScreen("John", tutors, expectedResultName = "John Smith")

        composeTestRule.onNodeWithText("John Smith").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    }

    @Test
    fun testEmptyResultsSearchFlow() {
        launchSearchScreen("UnknownName", emptyList())
        composeTestRule.onNodeWithText("John Smith").assertDoesNotExist()
    }

    @Test
    fun testSpecificStringMatchFlow() {
        // We pass "Orian" so the test waits for it to appear
        launchSearchScreen("Ori", listOf(User("u1", "Orian", role = UserRole.TUTOR)), expectedResultName = "Orian")

        composeTestRule.onNodeWithText("Orian").assertIsDisplayed()
        composeTestRule.onNodeWithText("Or").assertDoesNotExist()
    }
}