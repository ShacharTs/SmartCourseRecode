package com.smartcourse.ui.screens.setting.terms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndServiceScreen(
    showBackButton: Boolean,
    requireAcceptance: Boolean,
    onAccepted: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    // Track the scroll state of the main column
    val scrollState = rememberScrollState()

    // State for the checkbox
    var accepted by rememberSaveable { mutableStateOf(false) }

    // Logic to determine if user has reached the end of the text.
    // We use derivedStateOf to optimize performance (only re-calculates when needed).
    val hasReachedBottom by remember {
        derivedStateOf {
            // If the content is short enough to fit without scrolling, maxValue is 0
            if (scrollState.maxValue == 0) {
                true
            } else {
                // Check if current scroll value is at or very near the maximum
                scrollState.value >= (scrollState.maxValue - 5)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Service") },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (requireAcceptance) {
                Surface(shadowElevation = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = accepted,
                                // Checkbox is disabled until user scrolls to the bottom
                                enabled = hasReachedBottom,
                                onCheckedChange = { accepted = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "I have read and agree to the Terms & Service",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (hasReachedBottom)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onAccepted,
                            // Button requires both scrolling to bottom AND checking the box
                            enabled = accepted && hasReachedBottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (hasReachedBottom) "Continue"
                                else "Please scroll to the bottom"
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scrollState) // Attach our scroll state here
        ) {

            //  Joke section - Unofficial Disclaimer
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("Completely Unofficial Disclaimer")
                    Spacer(modifier = Modifier.height(8.dp))
                    TermItem("By using this app, this project automatically received a perfect score (100) in the Software Engineering course.")
                    TermItem("This claim is not verified, but we feel confident about it.")
                    TermItem("If you are reading this, you are clearly thorough and deserve extra credit.")
                    TermItem("Any resemblance to a flawless project is absolutely intentional.")
                }
            }

            TermsSection("General") {
                TermItem("SmartCourse is provided as-is, without guarantees of uninterrupted availability.")
                TermItem("By using the app, you acknowledge that bugs and unexpected behavior may occur.")
            }

            TermsSection("Purpose of the App") {
                TermItem("SmartCourse is an educational platform designed to help students and tutors discover courses.")
                TermItem("The app is intended for academic and informational use only.")
            }

            TermsSection("Liability Disclaimer") {
                TermItem("SmartCourse is not responsible for missed deadlines or academic outcomes.")
                TermItem("Use of the app is at your own discretion and risk.")
            }

            TermsSection("Development Reality") {
                TermItem("This app was built under academic pressure and caffeine.")
                TermItem("Errors may be logged. Whether they are fixed immediately depends on reality.")
                TermItem("Performance may degrade after adding \"just one more feature\".")
            }

            TermsSection("Changes to These Terms") {
                TermItem("These terms may be updated as the app evolves.")
                TermItem("Continued use after changes implies acceptance of the updated terms.")
            }

            // Visual indicator that the end has been reached
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "End of Document - Works on my machine™",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Extra spacing to ensure the "End of Document" is clearly visible
            // and allows the scroll state to hit the absolute max value.
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun TermItem(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun TermsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        SectionTitle(title)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}