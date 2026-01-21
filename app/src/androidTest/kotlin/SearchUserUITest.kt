import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
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

    // MockK can mock final classes without the 'open' keyword
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val authRepository = mockk<AuthRepository>(relaxed = true)

    @Test
    fun testSuccessfulSearchFlow() {
        // 1. Setup Mock Data
        val me = User(userId = "me", name = "Me", role = UserRole.STUDENT)
        val tutor = User(userId = "t1", name = "John Smith", role = UserRole.TUTOR)

        coEvery { authRepository.restoreValidSession() } returns me
        coEvery { userRepository.searchUsers("John") } returns listOf(tutor)
        coEvery { userRepository.getUserCourses(any()) } returns emptyList()

        // 2. Initialize ViewModel
        val viewModel = SearchUserViewModel(userRepository, authRepository)

        composeTestRule.setContent {
            val navController = rememberNavController()
            SearchUserScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // 3. Action: Search for "John"
        composeTestRule
            .onNode(
                (hasText("Search by name", ignoreCase = true) or hasAnyChild(hasText("Search by name")))
                        and hasSetTextAction()
            ).performTextInput("John")

        // --- PAUSE 1: View the typed text ---
        // This allows you to see the "John" query in the search bar
        Thread.sleep(2000)

        // 4. Verification
        composeTestRule.onNodeWithText("John Smith")
            .assertIsDisplayed()

        // --- PAUSE 2: View the search results ---
        // This keeps the screen open for 5 seconds so you can see the "John Smith" card
        Thread.sleep(5000)
    }
}