package com.smartcourse.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.R
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LoginColorPalette
import com.smartcourse.ui.theme.LoginScreenColors
import kotlinx.coroutines.launch


enum class ButtonVariant { PRIMARY, OUTLINE, GOOGLE }

@Composable
fun LoginScreen(
    navController: NavController,
    loginVM: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val colors = if (isDark) LoginScreenColors.Dark else LoginScreenColors.Light

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    val (emailFocusRequester, passwordFocusRequester) = remember { FocusRequester.createRefs() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Consolidated logic for handling auth results
    val handleAuthResult: (Result<Unit>) -> Unit = { result ->
        if (result.isFailure) {
            loginError = result.exceptionOrNull()?.message ?: "Authentication failed"
        } else {
            loginError = null
        }
    }

    val onLogin: () -> Unit = {
        scope.launch {
            val result = loginVM.loginWithEmail(email, password, context)
            if (result.isFailure) {
                email = ""
                password = ""
            }
            handleAuthResult(result)
        }
    }

    val onGoogleLogin: () -> Unit = {
        scope.launch {
            handleAuthResult(loginVM.loginWithGoogle(context))
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
            .background(if (isDark) Color.Black else Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.verticalGradient(if (isDark) AppGradients.Dark else AppGradients.Light))
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
                LoginHeader(loginColors = loginColors)
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

                Spacer(modifier = Modifier.height(50.dp))

                LoginButtons(
                    loginError = loginError,
                    onLogin = onLogin,
                    onGoogleLogin = onGoogleLogin,
                    onNavigateToRegister = onNavigateToRegister,
                    loginColors = loginColors,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun LoginHeader(loginColors: LoginColorPalette) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Nice to see you again", color = loginColors.text, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(R.drawable.smartcourse_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp).clip(RoundedCornerShape(24.dp)),
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
    loginColors: LoginColorPalette
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        AppTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Email",
            focusRequester = emailFocusRequester,
            imeAction = ImeAction.Next,
            onAction = { passwordFocusRequester.requestFocus() },
            colors = loginColors
        )

        AppTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Password",
            isPassword = true,
            showPassword = showPassword,
            onTogglePassword = onTogglePassword,
            focusRequester = passwordFocusRequester,
            imeAction = ImeAction.Done,
            onAction = onLogin,
            colors = loginColors
        )
    }
}

@Composable
private fun LoginButtons(
    loginError: String?,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    loginColors: LoginColorPalette,
    isDark: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // Error Message
        if (loginError != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(loginColors.errorBackground)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = loginError, color = loginColors.errorText, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        // Action Buttons
        AppButton(text = "Sign in", variant = ButtonVariant.PRIMARY, onClick = onLogin)
        AppButton(text = "Register", variant = ButtonVariant.OUTLINE, onClick = onNavigateToRegister)

        Spacer(modifier = Modifier.height(10.dp))

        AppButton(
            text = "Continue with Google",
            variant = ButtonVariant.GOOGLE,
            onClick = onGoogleLogin,
            isDark = isDark
        )
    }
}



@Composable
fun AppButton(
    text: String,
    variant: ButtonVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val shape = RoundedCornerShape(12.dp)
    val purple = Color(0xFF6B21A8)

    val buttonModifier = modifier
        .fillMaxWidth(0.85f)
        .height(if (variant == ButtonVariant.GOOGLE) 50.dp else 54.dp)
        .clip(shape)
        .clickable { onClick() }
        .then(
            when (variant) {
                ButtonVariant.PRIMARY -> Modifier.background(purple)
                ButtonVariant.OUTLINE -> Modifier.border(1.5.dp, purple, shape)
                ButtonVariant.GOOGLE -> {
                    val bg = if (isDark) Color(0xFF2D2D2D) else Color.White
                    val border = if (isDark) Color(0xFF444444) else Color(0xFFE5E7EB)
                    Modifier.shadow(1.dp, shape).border(1.dp, border, shape).background(bg)
                }
            }
        )

    Box(modifier = buttonModifier, contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (variant == ButtonVariant.GOOGLE) {
                Icon(painter = painterResource(R.drawable.google_icon), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(18.dp))
            }
            Text(
                text = text,
                color = when (variant) {
                    ButtonVariant.PRIMARY -> Color.White
                    ButtonVariant.OUTLINE -> purple
                    ButtonVariant.GOOGLE -> if (isDark) Color.White else Color(0xFF374151)
                },
                fontSize = if (variant == ButtonVariant.GOOGLE) 14.sp else 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    focusRequester: FocusRequester,
    imeAction: ImeAction,
    onAction: () -> Unit,
    colors: LoginColorPalette,
    isPassword: (Boolean) = false,
    showPassword: (Boolean) = false,
    onTogglePassword: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = colors.placeholder) },
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = colors.placeholder
                    )
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onAction() }),
        modifier = Modifier.fillMaxWidth().height(56.dp).focusRequester(focusRequester),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.fieldFocused,
            unfocusedBorderColor = colors.fieldBorder,
            cursorColor = colors.fieldFocused,
            focusedTextColor = colors.text,
            unfocusedTextColor = colors.text
        )
    )
}