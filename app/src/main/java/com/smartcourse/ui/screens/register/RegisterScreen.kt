package com.smartcourse.ui.screens.register

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.R
import com.smartcourse.auth.AuthState
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.components.*
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // REACT TO AUTH STATE CHANGE
    val authState by remember { derivedStateOf { authViewModel.authState } }

    LaunchedEffect(authState) {
        if (authState == AuthState.REGISTERED) {
            navController.navigate(Screen.ChooseRole.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    CustomColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(24.dp)
    ) {

        registerUpperArea()

        CustomSpacer(height = 20)

        registerMidArea(
            email = email,
            password = password,
            confrimPassword = confirmPassword,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onConfirmPasswordChange = { confirmPassword = it }
        )

        CustomSpacer(height = 20)

        registerForm(
            navController = navController,
            registerError = registerError,
            onRegister = {
//                scope.launch {
//
//                    // 🔍 VALIDATE FIRST
//                    val validation = authViewModel.validateRegistration(
//                        email = email,
//                        password = password,
//                        confirmPassword = confirmPassword
//                    )
//
//                    if (!validation.success) {
//                        registerError = validation.error
//                        return@launch
//                    }
//
//                    registerError = null
//
//
//                    val regSuccess = authViewModel.registerEmail(email, password)
//                    if (!regSuccess) {
//                        registerError = "Could not register"
//                        return@launch
//                    }
//
//
//                    val loginSuccess = authViewModel.loginEmail(email, password)
//                    if (!loginSuccess) {
//                        registerError = "Could not log in after registration"
//                        return@launch
//                    }
//                }
            }
        )
    }
}


@Composable
private fun registerForm(
    navController: NavController,
    registerError: String? = null,
    onRegister: () -> Unit
) {
    CustomRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.Center
    ) {
        CustomBox(
            onClick = {
                navController.navigate(Screen.Login.route)
            }
        ) {
            val signUpColor = if (isSystemInDarkTheme()) Color.Yellow else Color.Blue

            CustomText(
                text = "Already have an account?",
                color = signUpColor
            )
        }
    }

    CustomSpacer(height = 30)

    CustomButton(
        text = "Register",
        modifier = Modifier.fillMaxWidth(),
        onClick = onRegister
    )

    if (registerError != null) {
        CustomSpacer(height = 12)
        CustomText(
            text = registerError,
            color = Color.Red,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun registerMidArea(
    email: String,
    password: String,
    confrimPassword: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    CustomRow(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomColumn {

            // EMAIL FIELD
            CustomText(
                text = "Email",
                modifier = Modifier.padding(bottom = 10.dp),
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                singleLine = true,
                placeholder = { CustomText("email@example.com") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            CustomSpacer(height = 20)

            // PASSWORD FIELD
            CustomText(
                text = "Password",
                modifier = Modifier.padding(bottom = 10.dp),
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                singleLine = true,
                placeholder = { CustomText("Enter password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )

            CustomSpacer(height = 20)

            // CONFIRM PASSWORD FIELD
            CustomText(
                text = "Confirm Password",
                modifier = Modifier.padding(bottom = 10.dp),
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = confrimPassword,
                onValueChange = onConfirmPasswordChange,
                singleLine = true,
                placeholder = { CustomText("Re-enter password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                visualTransformation =
                    if (confirmPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun registerUpperArea() {
    CustomText(
        text = "Register",
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
