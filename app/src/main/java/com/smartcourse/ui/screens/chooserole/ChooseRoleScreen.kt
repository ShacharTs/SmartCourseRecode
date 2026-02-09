package com.smartcourse.ui.screens.chooserole

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.R
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.ui.theme.ChooseRoleColorPalette
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun ChooseRoleScreen(
    navController: NavController,
    chooseRoleViewModel: ChooseRoleViewModel = hiltViewModel(),
) {
    val authVM: AuthViewModel = hiltViewModel()
    val palette = LocalAppPalette.current
    val colors = palette.chooseRole

    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors.backgroundGradient))
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "Choose your role",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This helps us personalize your experience",
                fontSize = 15.sp,
                color = colors.subtext
            )

            Spacer(modifier = Modifier.height(32.dp))


            RoleSelectionList(
                selected = selectedRole,
                colors = colors,
                onSelect = { selectedRole = it }
            )

            Spacer(modifier = Modifier.height(48.dp))


            Button(
                onClick = {
                    chooseRoleViewModel.updateUserRole(selectedRole) {
                        authVM.onRoleChosen()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(text = "Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RoleSelectionList(
    selected: UserRole,
    colors: ChooseRoleColorPalette,
    onSelect: (UserRole) -> Unit
) {
    val options = listOf(
        UserRole.STUDENT to R.drawable.student to "Find tutors and manage your courses",
        UserRole.TUTOR to R.drawable.teacher to "Teach, mentor, and help students"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        options.forEach { (roleData, description) ->
            val (role, icon) = roleData
            RoleCard(
                role = role,
                icon = icon,
                description = description,
                selected = selected == role,
                colors = colors,
                onClick = { onSelect(role) }
            )
        }
    }
}

@Composable
private fun RoleCard(
    role: UserRole,
    icon: Int,
    description: String,
    selected: Boolean,
    colors: ChooseRoleColorPalette,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) colors.accent.copy(alpha = 0.14f) else colors.card

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(colors.accent)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = role.name.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = colors.subtext
            )
        }

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = colors.accent,
                unselectedColor = colors.subtext
            )
        )
    }
}