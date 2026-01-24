package com.smartcourse.ui.screens.chat.util

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.smartcourse.ui.screens.chat.vm.LocationViewModel


@SuppressLint("MissingPermission")
fun fetchCurrentLocation(
    client: FusedLocationProviderClient,
    vm: LocationViewModel
) {
    client.lastLocation
        .addOnSuccessListener { it?.let { loc ->
            vm.onLocationReceived(loc.latitude, loc.longitude)
        } ?: requestFreshLocation(client, vm) }
        .addOnFailureListener { vm.onError(it.message ?: "Location error") }
}

@SuppressLint("MissingPermission")
private fun requestFreshLocation(
    client: FusedLocationProviderClient,
    vm: LocationViewModel
) {
    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { it?.let { loc ->
            vm.onLocationReceived(loc.latitude, loc.longitude)
        } ?: vm.onError("No GPS data") }
}
