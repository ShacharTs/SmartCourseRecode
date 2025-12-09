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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomButton
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomRow
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf<String?>(null) }

    // Observe registration result from ViewModel
    val signUpResult by authViewModel.signUpState.collectAsState()

    // React to registration result safely
    LaunchedEffect(signUpResult) {
        if (signUpResult == null) return@LaunchedEffect

        if (signUpResult!!.success) {
            registerError = null
            authViewModel.setRegister()
            // navController.navigate(Screen.ChooseRole.route)
        } else {
            registerError = signUpResult!!.error ?: "Registration failed"
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
            authViewModel = authViewModel,
            onRegister = {

                // 1. Validate using the new validateRegistration()
                val validation = authViewModel.validateRegistration(
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )

                if (!validation.success) {
                    registerError = validation.error
                    return@registerForm
                }

                // 2. Safe to continue → Perform signup + login
                authViewModel.registerAndLogin(email, password)

            },
            registerError = registerError
        )

    }
}


@Composable
private fun registerForm(
    navController: NavController,
    authViewModel: AuthViewModel,
    onRegister: () -> Unit,
    registerError: String? = null
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
            val signUpColor = if (isSystemInDarkTheme()) {
                Color.Yellow
            } else {
                Color.Blue
            }

            CustomText(
                text = "Already have account ?",
                color = signUpColor
            )
        }
    }

    CustomSpacer(height = 30)

    CustomButton(
        text = "Register",
        modifier = Modifier.fillMaxWidth(),
        onClick = { onRegister() }
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
        CustomColumn{
            emailField(email, onEmailChange)

            CustomSpacer(height = 20)

            PasswordField(
                password = password,
                onPasswordChange = onPasswordChange,
                passwordVisible = passwordVisible,
                onPasswordVisibleChange = { passwordVisible = it }
            )


            CustomSpacer(height = 20)

            ConfirmPasswordField(
                confirmPassword = confrimPassword,
                onConfirmPasswordChange = onConfirmPasswordChange,
                confirmPasswordVisible = confirmPasswordVisible,
                onConfirmPasswordVisibleChange = { confirmPasswordVisible = it }
            )

        }
    }
}

@Composable
private fun emailField(
    email: String,
    onEmailChange: (String) -> Unit
) {
    CustomText(
        text = "Email",
        modifier = Modifier.padding(bottom = 10.dp),
        fontWeight = FontWeight.Bold
    )
    CustomSpacer(height = 4)

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        singleLine = true,
        placeholder = {
            CustomText(
                text = "email@example.com",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
    )
}


@Composable
private fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit
) {
    CustomText(
        text = "Password",
        modifier = Modifier.padding(bottom = 10.dp),
        fontWeight = FontWeight.Bold
    )
    CustomSpacer(height = 4)

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        singleLine = true,
        placeholder = {
            CustomText(
                text = "Enter password",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        visualTransformation =
            if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
                onPasswordVisibleChange(!passwordVisible)
            }) {
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
}


@Composable
private fun ConfirmPasswordField(
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit
) {
    CustomText(
        text = "Confirm Password",
        modifier = Modifier.padding(bottom = 10.dp),
        fontWeight = FontWeight.Bold
    )
    CustomSpacer(height = 4)

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        singleLine = true,
        placeholder = {
            CustomText(
                text = "Re-enter password",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        visualTransformation =
            if (confirmPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
                onConfirmPasswordVisibleChange(!confirmPasswordVisible)
            }) {
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

