package com.smartcourse.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.R
import com.smartcourse.ui.screens.components.*
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun RegisterScreen(
    registerVM: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    // 1. Access the unified register palette
    val palette = LocalAppPalette.current
    val registerColors = palette.register
    val isDark = palette.isDark

    // 2. Use the unified brand gradient
    val backgroundBrush = Brush.verticalGradient(
        if (isDark) AppGradients.Dark else AppGradients.Light
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val uiState by registerVM.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onRegisterSuccess()
        }
    }

    CustomColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Pass registerColors down to the title area
        RegisterUpperArea(registerColors.text)

        CustomSpacer(height = 20)

        RegisterMidArea(
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onConfirmPasswordChange = { confirmPassword = it }
        )

        CustomSpacer(height = 20)

        RegisterForm(
            isLoading = uiState.isLoading,
            error = uiState.error,
            onNavigateBack = onNavigateBack,
            onRegister = {
                val error = registerVM.validate(email, password, confirmPassword)
                if (error != null) registerVM.setError(error) else registerVM.register(email, password)
            }
        )
    }
}

@Composable
private fun RegisterForm(
    isLoading: Boolean,
    error: String?,
    onRegister: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val registerColors = LocalAppPalette.current.register

    CustomRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomBox(onClick = onNavigateBack) {
            CustomText(
                text = "Already have account?",
                color = registerColors.fieldFocused // Using brand color for the link
            )
        }
    }

    CustomSpacer(height = 30)

    CustomButton(
        text = if (isLoading) "Registering..." else "Register",
        modifier = Modifier.fillMaxWidth(),
        onClick = onRegister
    )

    if (error != null) {
        CustomSpacer(height = 12)
        CustomText(
            text = error,
            color = registerColors.errorText, // Use palette error color
            fontSize = 14.sp
        )
    }
}

@Composable
private fun RegisterMidArea(
    email: String,
    password: String,
    confirmPassword: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    CustomColumn {
        EmailField(email, onEmailChange)
        CustomSpacer(height = 20)
        PasswordField(password, onPasswordChange, passwordVisible) { passwordVisible = it }
        CustomSpacer(height = 20)
        ConfirmPasswordField(confirmPassword, onConfirmPasswordChange, confirmPasswordVisible) { confirmPasswordVisible = it }
    }
}

@Composable
private fun EmailField(email: String, onEmailChange: (String) -> Unit) {
    val colors = LocalAppPalette.current.register

    CustomText(text = "Email", color = colors.text, fontWeight = FontWeight.Bold)
    CustomSpacer(height = 8)

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        singleLine = true,
        placeholder = { Text("email@example.com", color = colors.placeholder) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.fieldFocused,
            unfocusedBorderColor = colors.fieldBorder,
            focusedTextColor = colors.text,
            unfocusedTextColor = colors.text
        )
    )
}

@Composable
private fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit
) {
    val colors = LocalAppPalette.current.register

    CustomText(text = "Password", color = colors.text, fontWeight = FontWeight.Bold)
    CustomSpacer(height = 8)

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        singleLine = true,
        placeholder = { Text("Enter password", color = colors.placeholder) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onVisibleChange(!visible) }) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = colors.fieldFocused
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.fieldFocused,
            unfocusedBorderColor = colors.fieldBorder,
            focusedTextColor = colors.text,
            unfocusedTextColor = colors.text
        )
    )
}

@Composable
private fun ConfirmPasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit
) {
    val colors = LocalAppPalette.current.register

    CustomText(text = "Confirm Password", color = colors.text, fontWeight = FontWeight.Bold)
    CustomSpacer(height = 8)

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        singleLine = true,
        placeholder = { Text("Re-enter password", color = colors.placeholder) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onVisibleChange(!visible) }) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = colors.fieldFocused
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.fieldFocused,
            unfocusedBorderColor = colors.fieldBorder,
            focusedTextColor = colors.text,
            unfocusedTextColor = colors.text
        )
    )
}

@Composable
private fun RegisterUpperArea(textColor: androidx.compose.ui.graphics.Color) {
    CustomText(
        text = "Register",
        color = textColor,
        fontSize = 32.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        fontWeight = FontWeight.Bold
    )
    CustomBox(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.smartcourse_logo),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
    }
}