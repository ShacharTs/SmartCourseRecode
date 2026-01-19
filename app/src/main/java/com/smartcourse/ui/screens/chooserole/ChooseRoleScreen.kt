package com.smartcourse.ui.screens.chooserole

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.R
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomRow
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.theme.ChooseRoleColorPalette
import com.smartcourse.ui.theme.LocalAppPalette


@Composable
fun ChooseRoleScreen(
    navController: NavController,
    chooseRoleViewModel: ChooseRoleViewModel = hiltViewModel(),
    //authVM: AuthViewModel
) {
    val authVM: AuthViewModel = hiltViewModel()

    val palette = LocalAppPalette.current
    val colors = palette.chooseRole

    var role by remember { mutableStateOf(UserRole.STUDENT) }

    CustomBox(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors.backgroundGradient)
            )
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        CustomColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            TitleSection(colors)

            CustomSpacer(height = 32)

            ChooseUserRole(
                selected = role,
                colors = colors,
                onSelect = { role = it }
            )

            CustomSpacer(height = 48)

            ButtonsLowerPart(
                chooseRoleViewModel = chooseRoleViewModel,
                authVM = authVM,
                role = role.name
            )
        }
    }
}





@Composable
private fun TitleSection(colors: ChooseRoleColorPalette) {
    CustomText(
        text = "Choose your role",
        fontSize = 32.sp,
        color = colors.textPrimary
    )

    CustomSpacer(height = 8)

    CustomText(
        text = "This helps us personalize your experience",
        fontSize = 15.sp,
        color = colors.subtext
    )
}



@Composable
private fun ButtonsLowerPart(
    chooseRoleViewModel: ChooseRoleViewModel,
    authVM: AuthViewModel,
    role: String
) {
    val colors = LocalAppPalette.current.chooseRole

    Button(
        onClick = {
            chooseRoleViewModel.updateUserRole(
                UserRole.valueOf(role)
            ) {
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
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = "Continue",
            fontSize = 16.sp
        )
    }
}


@Composable
private fun Title() {
    // Title
    CustomText(
        text = "Choose Your Role",
        fontSize = 34.sp
    )
}


@Composable
fun ChooseUserRole(
    selected: UserRole,
    colors: ChooseRoleColorPalette,
    onSelect: (UserRole) -> Unit
) {
    val options = listOf(
        UserRole.STUDENT to R.drawable.student,
        UserRole.TUTOR to R.drawable.teacher
    )

    CustomColumn {
        options.forEach { (role, icon) ->
            RoleCard(
                role = role,
                icon = icon,
                selected = selected == role,
                colors = colors,
                onClick = { onSelect(role) }
            )
            CustomSpacer(height = 16)
        }
    }
}


@Composable
private fun RoleCard(
    role: UserRole,
    icon: Int,
    selected: Boolean,
    colors: ChooseRoleColorPalette,
    onClick: () -> Unit
) {
    val background =
        if (selected) colors.accent.copy(alpha = 0.14f)
        else colors.card

    CustomRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(16.dp))
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

        CustomSpacer(width = 16)

        CustomColumn(modifier = Modifier.weight(1f)) {
            CustomText(
                text = role.name.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 18.sp,
                color = colors.textPrimary
            )

            CustomText(
                text = when (role) {
                    UserRole.STUDENT -> "Find tutors and manage your courses"
                    UserRole.TUTOR -> "Teach, mentor, and help students"
                    UserRole.ADMIN -> TODO()
                    UserRole.TEMP -> TODO()
                },
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









