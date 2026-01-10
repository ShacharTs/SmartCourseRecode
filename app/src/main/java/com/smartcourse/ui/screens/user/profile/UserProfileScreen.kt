package com.smartcourse.ui.screens.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.StudentHomeColorPalette

@Composable
fun UserProfileScreen(
    navController: NavController,
    authVM: AuthViewModel
) {
    val palette = LocalAppPalette.current
    val home = palette.home
    val user = authVM.currentUser.value?.user ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (palette.isDark)
                        listOf(
                            Color(0xFF0B0514),
                            Color(0xFF1E0938),
                            Color(0xFF3A0F54)
                        )
                    else AppGradients.Light
                )
            )
            .statusBarsPadding()
            .padding(bottom = 24.dp)
    ) {

        /* ======================
           TOP TITLE
           ====================== */
        Text(
            text = "Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = home.textPrimary,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 12.dp)
        )

        /* ======================
           PROFILE CARD + AVATAR
           ====================== */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(home.card)
                    .padding(top = 64.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = user.name ?: "",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = home.textPrimary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = user.role?.name
                        ?.lowercase()
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "",
                    fontSize = 14.sp,
                    color = home.subtext
                )
            }

            // Avatar (cutting card)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(home.accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name
                        ?.split(" ")
                        ?.take(2)
                        ?.joinToString("") { it.first().uppercase() }
                        ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ======================
           BIO
           ====================== */
        SectionTitle("Bio", home)

        CardSection {
            Text(
                text = user.bio ?: "No bio available yet.",
                fontSize = 14.sp,
                color = home.subtext,
                lineHeight = 20.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        /* ======================
           COURSES
           ====================== */
        SectionTitle("Courses", home)

        CardSection {
            listOf(
                "Algorithms 1",
                "Data Structures",
                "Operating Systems"
            ).forEach {
                Text(
                    text = "• $it",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = home.textPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ======================
           CHANGE PROFILE
           ====================== */
        PrimaryButton(
            text = "Change Profile",
            color = Color(0xFFEC4899)
        ) {
            // TODO
        }

        Spacer(Modifier.height(12.dp))

        /* ======================
           LOGOUT
           ====================== */
        PrimaryButton(
            text = "Logout",
            color = Color(0xFFB71C1C)
        ) {
            authVM.logout()
        }
    }
}


@Composable
private fun CardSection(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF241636))
            .padding(16.dp),
        content = content
    )
}


@Composable
private fun SectionTitle(
    text: String,
    home: StudentHomeColorPalette
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = home.textPrimary,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )
}


@Composable
private fun PrimaryButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
private fun SectionHeader(
    title: String,
    onEditClick: () -> Unit,
    home: StudentHomeColorPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = home.textPrimary
        )

        Text(
            text = "Edit",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = home.accent,
            modifier = Modifier.clickable { onEditClick() }
        )
    }
}




@Composable
private fun ProfileRow(
    label: String,
    value: String,
    palette: StudentHomeColorPalette
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = palette.subtext
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = palette.textPrimary
        )
    }
}

