package com.smartcourse.ui.screens.chooserole

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.R
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomButton
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomRow
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.viewmodels.ChooseRoleViewModel


@Composable
fun ChooseRoleScreen(
    authViewModel: AuthViewModel,
    chooseRoleViewModel: ChooseRoleViewModel
) {
    var role by remember { mutableStateOf(UserRole.STUDENT.name) }

    CustomColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title()

        CustomSpacer(height = 40)

        // Centered selection box
        CustomBox(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color(0xFF262626).copy(alpha = 0.4f)) // nicer dark box
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            ChooseUserRole(
                selected = role,
                onSelect = { role = it }
            )
        }

        CustomSpacer(height = 80)

        buttonsLowerPart(chooseRoleViewModel, role, authViewModel)
    }
}

@Composable
private fun buttonsLowerPart(
    chooseRoleViewModel: ChooseRoleViewModel,
    role: String,
    authViewModel: AuthViewModel
) {
    // Confirm Button
    CustomButton(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(vertical = 8.dp),
        text = "Confirm",
        onClick = {
            chooseRoleViewModel.updateUserRole(UserRole.valueOf(role))
        }


    )

    // Back Button
    CustomButton(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(vertical = 8.dp),
        text = "Go back",
        onClick = {
            //navController.popBackStack()
            authViewModel.setLoggedOut()
        }
    )
}

@Composable
private fun title() {
    // Title
    CustomText(
        text = "Choose Your Role",
        fontSize = 34.sp
    )
}


@Composable
fun ChooseUserRole(
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = listOf(UserRole.STUDENT.name, UserRole.TUTOR.name)

    CustomColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        options.forEach { role ->
            CustomRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(role) }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = selected == role,
                    onClick = { onSelect(role) },
                    modifier = Modifier.size(24.dp)
                )

                CustomSpacer(width = 12)

                CustomText(
                    text = role,
                    fontSize = 18.sp
                )

                // keeps layout aligned for future image
                CustomSpacer(modifier = Modifier.weight(1f))


                CustomSpacer(width = 36, height = 36)

                Image(
                    painter = painterResource(
                        id = if (role == UserRole.STUDENT.name){
                            R.drawable.ic_search // placeholder

                        }
                        else {
                            R.drawable.ic_settings // place holder
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )



            }

            CustomSpacer(height = 8)
        }
    }
}




