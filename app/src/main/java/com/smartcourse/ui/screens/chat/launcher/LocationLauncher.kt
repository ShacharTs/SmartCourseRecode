package com.smartcourse.ui.screens.chat.launcher

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.smartcourse.ui.screens.chat.util.fetchCurrentLocation
import com.smartcourse.ui.screens.chat.vm.LocationEvent
import com.smartcourse.ui.screens.chat.vm.LocationViewModel

@Composable
fun LocationLauncher(
    context: Context,
    locationVM: LocationViewModel
) {
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                fetchCurrentLocation(
                    LocationServices.getFusedLocationProviderClient(context),
                    locationVM
                )
            } else {
                locationVM.onError("Location permission denied")
            }
        }

    LaunchedEffect(Unit) {
        locationVM.events.collect { event ->
            if (event is LocationEvent.GetLocation) {
                val fineGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val coarseGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (fineGranted || coarseGranted) {
                    fetchCurrentLocation(
                        LocationServices.getFusedLocationProviderClient(context),
                        locationVM
                    )
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }
}
