package com.smartcourse.ui.screens.chat.launcher

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.smartcourse.ui.screens.chat.vm.CameraEvent
import com.smartcourse.ui.screens.chat.vm.CameraViewModel
import java.io.File


@Composable
fun CameraLauncher(
    context: Context,
    cameraVM: CameraViewModel
) {
    val tempUri = remember {
        val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) cameraVM.onPhotoCaptured(tempUri)
            else cameraVM.onError("Camera canceled")
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) cameraLauncher.launch(tempUri)
            else cameraVM.onError("Camera permission denied")
        }

    LaunchedEffect(Unit) {
        cameraVM.events.collect { event ->
            if (event is CameraEvent.OpenCamera) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (granted) cameraLauncher.launch(tempUri)
                else permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
