@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.chat.vm.CameraEvent
import com.smartcourse.ui.screens.chat.vm.CameraViewModel
import com.smartcourse.ui.screens.chat.vm.ChatViewModel
import com.smartcourse.ui.screens.chat.vm.GalleryEvent
import com.smartcourse.ui.screens.chat.vm.GalleryViewModel
import com.smartcourse.ui.screens.chat.vm.LocationEvent
import com.smartcourse.ui.screens.chat.vm.LocationViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ChatScreen(
    navController: NavController,
    chatVM: ChatViewModel = hiltViewModel(),
    cameraVM: CameraViewModel = hiltViewModel(),
    galleryVM: GalleryViewModel = hiltViewModel(),
    locationVM: LocationViewModel = hiltViewModel(),
    myId: String,
    chatId: String
) {
    var otherUser by remember { mutableStateOf<User?>(null) }
    var otherId by remember { mutableStateOf<String?>(null) }

    val messages by chatVM.messages.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    /* -------------------- LAUNCHERS -------------------- */

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { galleryVM.onImageSelected(it) }
    }

    // Camera Setup
    val tempPhotoUri = remember {
        val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraVM.onPhotoCaptured(tempPhotoUri)
        else cameraVM.onError("Camera canceled")
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(tempPhotoUri)
        else cameraVM.onError("Camera permission denied")
    }

    // Location Setup
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchCurrentLocation(fusedLocationClient, locationVM)
        } else {
            locationVM.onError("Location permission denied")
        }
    }


    // Observe the states from your ViewModels
    val cameraState by cameraVM.state.collectAsState()
    val galleryState by galleryVM.state.collectAsState()
    val locationState by locationVM.state.collectAsState()

// Trigger sending when a CAMERA photo is captured
    LaunchedEffect(cameraState.photoUri) {
        cameraState.photoUri?.let { uri ->
            val receiver = otherId ?: return@let
            chatVM.sendImageMessage(chatId, uri, myId, receiver)
            cameraVM.clear() // Clear state so it doesn't send again on recomposition
        }
    }

// Trigger sending when a GALLERY image is selected
    LaunchedEffect(galleryState.selectedImage) {
        galleryState.selectedImage?.let { uri ->
            val receiver = otherId ?: return@let
            chatVM.sendImageMessage(chatId, uri, myId, receiver)
            galleryVM.clear()
        }
    }


    // Trigger sending when a LOCATION is received
    var locationSent by remember { mutableStateOf(false) }

    LaunchedEffect(locationState.latitude, locationState.longitude) {
        if (locationSent) return@LaunchedEffect

        val lat = locationState.latitude
        val lng = locationState.longitude

        if (lat != 0.0 && lng != 0.0) {
            val receiver = otherId ?: return@LaunchedEffect
            chatVM.sendLocationMessage(chatId, lat, lng, myId, receiver)
            locationSent = true
        }
    }


    /* -------------------- EVENT COLLECTION -------------------- */

    LaunchedEffect(Unit) {
        // Collect Camera Events
        launch {
            cameraVM.events.collect { event ->
                if (event is CameraEvent.OpenCamera) {
                    val status =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (status == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(tempPhotoUri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }
        }

        // Collect Gallery Events
        launch {
            galleryVM.events.collect { event ->
                if (event is GalleryEvent.OpenGallery) {
                    galleryLauncher.launch("image/*")
                }
            }
        }

        // Collect Location Events (Missing in your previous version)
        launch {
            locationVM.events.collect { event ->
                if (event is LocationEvent.GetLocation) {
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

    /* -------------------- DATA SETUP -------------------- */

    LaunchedEffect(chatId) {
        try {
            // 1. Authenticate
            chatVM.ensureFirebaseReady()

            // 2. Start the listener immediately so messages load
            chatVM.startListening(chatId)

            // 3. Resolve user details for the TopBar
            val rId = chatVM.getReceiverId(chatId, myId)
            otherId = rId

            val both = chatVM.getBothUsers(chatId)
            val (_, other) = resolveUsers(myId, both)
            otherUser = other

        } catch (e: Exception) {
            Log.e("ChatScreen", "Setup failed: ${e.message}")
        }
    }

    // Add this inside ChatScreen
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    DisposableEffect(chatId) {
        onDispose { chatVM.stopListening() }
    }

    /* -------------------- UI -------------------- */

    ChatScaffold(
        navController = navController,
        otherUser = otherUser,
        otherId = otherId,
        chatVM = chatVM,
        chatId = chatId,
        myId = myId,
        messages = messages,
        listState = listState,
        onCameraClick = { cameraVM.requestCamera() },
        onGalleryClick = { galleryVM.requestGallery() },
        onLocationClick = { locationVM.requestLocation() }
    )
}


/**
 * Triggers the actual GPS fetch once permissions are confirmed.
 */
@SuppressLint("MissingPermission")
fun fetchCurrentLocation(
    client: FusedLocationProviderClient,
    vm: LocationViewModel
) {
    // Attempt to get the last known location for speed
    client.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                vm.onLocationReceived(location.latitude, location.longitude)
            } else {
                // If lastLocation is null, request a fresh one
                requestFreshLocation(client, vm)
            }
        }
        .addOnFailureListener { e ->
            vm.onError("Location Error: ${e.message}")
        }
}

@SuppressLint("MissingPermission")
private fun requestFreshLocation(
    client: FusedLocationProviderClient,
    vm: LocationViewModel
) {
    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                vm.onLocationReceived(location.latitude, location.longitude)
            } else {
                vm.onError("Could not retrieve GPS coordinates.")
            }
        }
        .addOnFailureListener { e ->
            vm.onError("GPS Error: ${e.message}")
        }
}