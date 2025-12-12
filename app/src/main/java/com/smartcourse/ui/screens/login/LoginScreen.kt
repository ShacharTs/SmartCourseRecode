package com.smartcourse.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
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
import androidx.navigation.NavController
import com.smartcourse.R
import com.smartcourse.ui.theme.screens.LoginColorPalette
import com.smartcourse.ui.theme.screens.LoginGradients
import com.smartcourse.ui.theme.screens.LoginScreenColors
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    loginVM: LoginViewModel,
    onNavigateToRegister: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val colors =
        if (isDark) LoginScreenColors.Dark else LoginScreenColors.Light

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    val (emailFocusRequester, passwordFocusRequester) =
        FocusRequester.createRefs()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val onLogin: () -> Unit = {
        scope.launch {
            val result = loginVM.loginWithEmail(
                email = email,
                password = password,
                context = context
            )

            if (result.isFailure) {
                email = ""
                password = ""
                loginError = result.exceptionOrNull()?.message
                    ?: "Invalid email or password"
            } else {
                loginError = null
            }
        }
    }

    // ✅ LOGIN WITH GOOGLE — UI DOES NOT KNOW STRATEGY
    val onGoogleLogin: () -> Unit = {
        scope.launch {
            val result = loginVM.loginWithGoogle(context)

            if (result.isFailure) {
                loginError =
                    result.exceptionOrNull()?.message
                        ?: "Google login failed"
            } else {
                loginError = null
            }
        }
    }

    LoginContent(
        isDark = isDark,
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
        passwordFocusRequester = passwordFocusRequester,
        loginColors = colors
    )
}


@Composable
private fun LoginContent(
    isDark: Boolean,
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
    passwordFocusRequester: FocusRequester,
    loginColors: LoginColorPalette
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Phone frame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        if (isDark) LoginGradients.Dark else LoginGradients.Light
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .statusBarsPadding()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                LoginHeader()

                Spacer(Modifier.height(32.dp))

                LoginFields(
                    email = email,
                    password = password,
                    showPassword = showPassword,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onTogglePassword = onTogglePassword,
                    onLogin = onLogin,
                    emailFocusRequester = emailFocusRequester,
                    passwordFocusRequester = passwordFocusRequester,
                    loginColors = loginColors
                )

                // Push buttons toward bottom naturally
                Spacer(modifier = Modifier.height(50.dp))

                LoginButtons(
                    loginError = loginError,
                    onLogin = onLogin,
                    onGoogleLogin = onGoogleLogin,
                    onNavigateToRegister = onNavigateToRegister,
                    loginColors = loginColors
                )
            }
        }
    }
}



@Composable
private fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
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
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    loginColors: LoginColorPalette,
    modifier: Modifier = Modifier
) {
    val placeholderColor = loginColors.placeholder
    val textColor = loginColors.text
    val fieldBorder = loginColors.fieldBorder
    val fieldFocused = loginColors.fieldFocused

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("Password", color = placeholderColor) },
            visualTransformation =
                if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector =
                            if (showPassword) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
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
    loginColors: LoginColorPalette
) {
    val errorBackgroundColor = loginColors.errorBackground
    val errorTextColor = loginColors.errorText

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .heightIn(min = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loginError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(errorBackgroundColor)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = loginError,
                        color = errorTextColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        GradientButton(
            text = "Sign in",
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(onClick = onLogin)
        )

        OutlineButton(
            text = "Register",
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable(onClick = onNavigateToRegister)
        )

        Spacer(modifier = Modifier.height(10.dp))

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