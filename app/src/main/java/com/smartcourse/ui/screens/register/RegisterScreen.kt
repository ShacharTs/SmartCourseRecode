package com.smartcourse.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcourse.R
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.RegisterColorPalette

@Composable
fun RegisterScreen(
    registerVM: RegisterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val palette = LocalAppPalette.current
    val registerColors = palette.register
    val isDark = palette.isDark

    val backgroundBrush = Brush.verticalGradient(
        if (isDark) AppGradients.Dark else AppGradients.Light
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    val uiState by registerVM.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterUpperArea(registerColors.text)

        Spacer(modifier = Modifier.height(20.dp))

        // Registration Fields
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            var passwordVisible by remember { mutableStateOf(false) }
            var confirmPasswordVisible by remember { mutableStateOf(false) }

            RegisterField(
                label = "Email",
                value = email,
                placeholder = "email@example.com",
                onValueChange = { email = it },
                colors = registerColors
            )

            RegisterField(
                label = "Password",
                value = password,
                placeholder = "Enter password",
                onValueChange = { password = it },
                isPassword = true,
                isVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
                colors = registerColors
            )

            RegisterField(
                label = "Confirm Password",
                value = confirmPassword,
                placeholder = "Re-enter password",
                onValueChange = { confirmPassword = it },
                isPassword = true,
                isVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                colors = registerColors
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        RegisterActions(
            isLoading = uiState.isLoading,
            error = uiState.error,
            onNavigateBack = onNavigateBack,
            onRegister = {
                val error = registerVM.validate(email, password, confirmPassword)
                if (error != null) registerVM.setError(error)
                else registerVM.register(email, password)
            },
            colors = registerColors
        )
    }
}

@Composable
private fun RegisterField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    colors: RegisterColorPalette,
    isPassword: (Boolean) = false,
    isVisible: (Boolean) = false,
    onToggleVisibility: () -> Unit = {}
) {
    Column {
        Text(text = label, color = colors.text, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text(placeholder, color = colors.placeholder) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            visualTransformation = if (isPassword && !isVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = colors.fieldFocused
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.fieldFocused,
                unfocusedBorderColor = colors.fieldBorder,
                focusedTextColor = colors.text,
                unfocusedTextColor = colors.text
            )
        )
    }
}

@Composable
private fun RegisterActions(
    isLoading: Boolean,
    error: String?,
    onRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    colors: RegisterColorPalette
) {
    Text(
        text = "Already have account?",
        color = colors.fieldFocused,
        modifier = Modifier.clickable { onNavigateBack() }
    )

    Spacer(modifier = Modifier.height(30.dp))

    Button(
        onClick = onRegister,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B21A8))
    ) {
        Text(
            text = if (isLoading) "Registering..." else "Register",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }

    if (error != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = error, color = colors.errorText, fontSize = 14.sp)
    }
}

@Composable
private fun RegisterUpperArea(textColor: Color) {
    Text(
        text = "Register",
        color = textColor,
        fontSize = 32.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        fontWeight = FontWeight.Bold
    )
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.smartcourse_logo),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
    }
}