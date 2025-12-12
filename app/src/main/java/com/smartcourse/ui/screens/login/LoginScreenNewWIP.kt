package com.smartcourse.ui.screens.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.R

@Composable
fun WelcomeScreen(isDark: Boolean) {

    // Detect dark mode
    //val isDark = isSystemInDarkTheme()

    val fieldBorder = if (isDark) Color(0xFFFFFFFF) else Color(0xFF020000)
    val fieldFocused = if (isDark) Color(0xFFB388FF) else Color(0xFF9333EA)
    val placeholderColor = if (isDark) Color(0xFFBBBBBB) else Color(0xFF000000)
    val textColor = if (isDark) Color.White else Color.Black



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {

        // PHONE FRAME
        Box(
            modifier = Modifier
                .size(width = 412.dp, height = 892.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        if (isDark) {
                            listOf(
                                Color(0xFF0B0514),
                                Color(0xFF1E0938),
                                Color(0xFF3A0F54)
                            )
                        } else {
                            listOf(
                                Color(0xFF9333EA),
                                Color(0xFFEC4899),
                                Color(0xFFF97316)
                            )
                        }
                    )
                )
        ) {

            /* TOP SECTION (TITLE + IMAGE) */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text(
                    text = "Nice to see you again",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // FIXED PERFECTLY CENTERED IMAGE
                Image(
                    painter = painterResource(R.drawable.smartcourselogo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(18.dp),
                    contentScale = ContentScale.Crop
                )
            }

            /* INPUT FIELDS */
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = placeholderColor) },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 330.dp, start = 30.dp)
                    .width(327.dp)
                    .height(56.dp),
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
                onValueChange = { password = it },
                placeholder = { Text("Password", color = placeholderColor) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 410.dp, start = 30.dp)
                    .width(327.dp)
                    .height(56.dp),
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


            /* BUTTONS */

            GradientButton(
                text = "Sign in",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 500.dp)
            )

            OutlineButton(
                text = "Register",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 565.dp)
            )

            GoogleButton(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 640.dp)
            )

            /* BOTTOM NAV HANDLE */
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


/* ------------------ BUTTON COMPONENTS ------------------ */

@Composable
fun GradientButton(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 340.dp, height = 50.dp)
            .clip(RoundedCornerShape(12.dp))
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
            .size(width = 340.dp, height = 50.dp)
            .clip(RoundedCornerShape(12.dp))
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
            .size(width = 340.dp, height = 48.dp)
            .clip(RoundedCornerShape(12.dp))
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


@Preview(showBackground = true)
@Composable
fun WelcomeScreenLightPreview() {
        WelcomeScreen(false)
}


@Preview(showBackground = true)
@Composable
fun WelcomeScreenDarkPreview() {
    WelcomeScreen(true)
}


