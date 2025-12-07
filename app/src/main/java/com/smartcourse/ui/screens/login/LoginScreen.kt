package com.smartcourse.ui.screens.login

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.R
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.auth.EmailAuthStrategy
import com.smartcourse.auth.GoogleAuthStrategy
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomButton
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomRow
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText
import kotlinx.coroutines.launch


// ------------------------------------------------------------
//  LOGIN SCREEN ENTRY POINT
// ------------------------------------------------------------
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var loginError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LoginContent(
        email = email,
        password = password,
        showPassword = showPassword,
        loginError = loginError,

        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onTogglePassword = { showPassword = !showPassword },

        onLogin = {
            scope.launch {
                val success = viewModel.loginWithResult(
                    EmailAuthStrategy(email, password, viewModel.supabase),
                    context = context
                )

                if (!success) {
                    // clear fields
                    email = ""
                    password = ""

                    // show error
                    loginError = "Invalid email or password"
                } else {
                    loginError = null
                }
            }
        },

        onGoogleLogin = {
            viewModel.login(
                GoogleAuthStrategy(viewModel.supabase),
                context = context
            )
        },

        onNavigateToRegister = {
            navController.navigate(Screen.Register.route)
        }
    )


}



// ------------------------------------------------------------
//  LOGIN CONTENT (LAYOUT ORGANIZATION)
// ------------------------------------------------------------
@Composable
private fun LoginContent(
    email: String,
    password: String,
    showPassword: Boolean,
    loginError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    CustomColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(24.dp)
    ) {

        LoginHeader()

        CustomSpacer(height = 30)

        LoginForm(
            email = email,
            password = password,
            showPassword = showPassword,
            loginError = loginError,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onTogglePassword = onTogglePassword,
            onLogin = onLogin,
            onGoogleLogin = onGoogleLogin,
            onNavigateToRegister = onNavigateToRegister
        )
    }
}




@Composable
private fun LoginHeader() {
    CustomText(
        //text = stringResource(id = R.string.welcome_back),
        text = "Welcome Back",
        fontSize = 32.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )

    CustomBox(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.smartcourselogo),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
    }
}



@Composable
private fun LoginForm(
    email: String,
    password: String,
    showPassword: Boolean,
    loginError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    CustomColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,

    ) {

        CustomText(
            text = "Email",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            singleLine = true,
            placeholder = {
                CustomText(
                    "email@example.com",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        CustomSpacer(height = 30)

        CustomText(
            text = "Password",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            singleLine = true,
            placeholder = {
                CustomText(
                    "Enter password",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            visualTransformation =
                if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        CustomSpacer(height = 40)

        // --------------------------------------------------------
        // LOGIN BUTTON
        // --------------------------------------------------------
        CustomButton(
            text = "Login",
            modifier = Modifier.fillMaxWidth(),
            onClick = onLogin
        )



        CustomSpacer(height = 40)

        // --------------------------------------------------------
        // GOOGLE LOGIN BUTTON
        // --------------------------------------------------------
        CustomButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Sign in with Google",
            icon = painterResource(R.drawable.google_icon),
            onClick = onGoogleLogin
        )

        CustomSpacer(height = 40)

        // --------------------------------------------------------
        // NAVIGATE TO REGISTER
        // --------------------------------------------------------
        CustomRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomText("Don't have an account?")

            val color = if (isSystemInDarkTheme()) Color.Yellow else Color.Blue

            CustomBox(onClick = onNavigateToRegister) {
                CustomText(
                    text = "Sign up",
                    color = color,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        CustomSpacer(height = 40)

        // --------------------------------------------------------
        // ERROR MESSAGE
        // --------------------------------------------------------
        if (loginError != null) {
            CustomSpacer(height = 16)
            CustomText(
                text = loginError,
                color = Color.Red,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}
