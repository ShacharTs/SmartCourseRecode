package com.smartcourse.ui.screens.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.R
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.auth.EmailAuthStrategy
import com.smartcourse.auth.GoogleAuthStrategy
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
) {
    val isDark: Boolean = isSystemInDarkTheme()

    val fieldBorder = if (isDark) Color(0xFFFFFFFF) else Color(0xFF020000)
    val fieldFocused = if (isDark) Color(0xFFB388FF) else Color(0xFF9333EA)
    val placeholderColor = if (isDark) Color(0xFFBBBBBB) else Color(0xFF000000)
    val textColor = if (isDark) Color.White else Color.Black

    /* State Management */
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    val (emailFocusRequester, passwordFocusRequester) = FocusRequester.createRefs()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    /* Login Logic */
    val onLogin: () -> Unit = {
        scope.launch {
            val success = authViewModel.loginWithResult(
                EmailAuthStrategy(email, password, authViewModel.supabase),
                context = context
            )
            if (!success) {
                email = ""
                password = ""
                loginError = "Invalid email or password"
            } else {
                loginError = null
            }
        }
    }

    val onGoogleLogin: () -> Unit = {
        authViewModel.login(
            GoogleAuthStrategy(authViewModel.supabase),
            context = context
        )
    }

    val onNavigateToRegister: () -> Unit = {
        authViewModel.setRegister()
    }

    LoginContent(
        isDark = isDark,
        fieldBorder = fieldBorder,
        fieldFocused = fieldFocused,
        placeholderColor = placeholderColor,
        textColor = textColor,
        email = email,
        password = password,
        showPassword = showPassword,
        loginError = loginError,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onTogglePassword = { showPassword = !showPassword },
        onLogin = onLogin,
        onGoogleLogin = onGoogleLogin,
        onNavigateToRegister = onNavigateToRegister,
        emailFocusRequester = emailFocusRequester,
        passwordFocusRequester = passwordFocusRequester
    )
}

@Composable
private fun LoginContent(
    isDark: Boolean,
    fieldBorder: Color,
    fieldFocused: Color,
    placeholderColor: Color,
    textColor: Color,
    email: String,
    password: String,
    showPassword: Boolean,
    loginError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Phone Frame Background
        Box(
            modifier = Modifier
                .size(width = 412.dp, height = 892.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        if (isDark) {
                            listOf(Color(0xFF0B0514), Color(0xFF1E0938), Color(0xFF3A0F54))
                        } else {
                            listOf(Color(0xFF9333EA), Color(0xFFEC4899), Color(0xFFF97316))
                        }
                    )
                )
        ) {

            LoginHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp)
            )

            LoginFields(
                email = email,
                password = password,
                showPassword = showPassword,
                placeholderColor = placeholderColor,
                textColor = textColor,
                fieldBorder = fieldBorder,
                fieldFocused = fieldFocused,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onTogglePassword = onTogglePassword,
                onLogin = onLogin,
                emailFocusRequester = emailFocusRequester,
                passwordFocusRequester = passwordFocusRequester,
                modifier = Modifier.align(Alignment.TopStart)
            )

            LoginButtons(
                loginError = loginError,
                onLogin = onLogin,
                onGoogleLogin = onGoogleLogin,
                onNavigateToRegister = onNavigateToRegister,
                modifier = Modifier.align(Alignment.TopCenter),
                isDark = isDark
            )

            // Bottom Nav Handle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .size(width = 108.dp, height = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
            )
        }
    }
}


@Composable
private fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nice to see you again",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.smartcourselogo),
            contentDescription = "Smart Course Logo",
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun LoginFields(
    email: String,
    password: String,
    showPassword: Boolean,
    placeholderColor: Color,
    textColor: Color,
    fieldBorder: Color,
    fieldFocused: Color,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    // Column to hold and space the input fields
    Column(
        modifier = modifier
            .width(327.dp)
            .offset(x = 30.dp, y = 330.dp)
    ) {
        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("Email", color = placeholderColor) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .focusRequester(emailFocusRequester),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = fieldFocused,
                unfocusedBorderColor = fieldBorder,
                cursorColor = fieldFocused,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedPlaceholderColor = placeholderColor,
                unfocusedPlaceholderColor = placeholderColor
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("Password", color = placeholderColor) },
            visualTransformation =
                if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = image,
                        contentDescription = "Toggle password visibility",
                        tint = placeholderColor
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onLogin() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .focusRequester(passwordFocusRequester),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = fieldFocused,
                unfocusedBorderColor = fieldBorder,
                cursorColor = fieldFocused,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedPlaceholderColor = placeholderColor,
                unfocusedPlaceholderColor = placeholderColor
            )
        )
    }
}

@Composable
private fun LoginButtons(
    loginError: String?,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean // Passed down for conditional styling
) {
    // Determine background color for the error text container in light mode
    val errorBackgroundColor = if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.5f)
    // Determine text color for better contrast in light mode
    val errorTextColor = if (isDark) Color.Red else Color(0xFFFF4444)

    // Vertical padding below the error message when present
    val errorPaddingBottom = 16.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = 475.dp), // Starting Y-offset for the first element
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ERROR MESSAGE - Visible ONLY when loginError is not null
        if (loginError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(errorBackgroundColor)
                    .padding(vertical = 4.dp), // Small padding around the error text
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = loginError,
                    color = errorTextColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            // Add space between the error and the first button
            Spacer(modifier = Modifier.height(errorPaddingBottom))
        }

        // Sign in Button
        GradientButton(
            text = "Sign in",
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(onClick = onLogin)
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Register Button
        OutlineButton(
            text = "Register",
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(onClick = onNavigateToRegister)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Google Login Button
        GoogleButton(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(onClick = onGoogleLogin)
        )
    }
}


/* ------------------ BUTTON COMPONENTS ------------------ */

@Composable
fun GradientButton(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .height(50.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF9333EA),
                        Color(0xFFEC4899)
                    )
                )
            )
            .shadow(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun OutlineButton(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .height(50.dp)
            .border(2.dp, Color(0xFF9333EA), RoundedCornerShape(12.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color(0xFF9333EA), fontSize = 16.sp)
    }
}

@Composable
fun GoogleButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .height(48.dp)
            .border(2.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.google_icon),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Text(
                "Sign in with Google",
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        }
    }
}