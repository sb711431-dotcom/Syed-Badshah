package com.example.vyro.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ui.theme.LocalVyroAccent
import com.example.ui.theme.VyroBlack
import com.example.ui.theme.VyroBorder
import com.example.ui.theme.VyroCardSurface
import com.example.ui.theme.VyroDarkSurface
import com.example.ui.theme.VyroElevatedSurface
import com.example.ui.theme.VyroGreenCloseCircle
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.ui.theme.VyroTextTertiary
import com.example.vyro.data.VyroRepository

data class CameraFilterPreset(
  val id: String,
  val name: String,
  val tintColor: Color,
  val overlayAlpha: Float
)

@Composable
fun CaptureScreen(
  onMediaPublished: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val accent = LocalVyroAccent.current

  // Permissions & Fallback state
  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasCameraPermission = isGranted
  }

  // Gallery Picker
  var selectedMediaUri by remember { mutableStateOf<String?>(null) }
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedMediaUri = uri.toString()
    }
  }

  // Camera Settings
  var isFlashOn by remember { mutableStateOf(false) }
  var isFrontCamera by remember { mutableStateOf(false) }
  var timerSeconds by remember { mutableStateOf(0) } // 0, 3, 10

  // Filter Presets
  val filterPresets = listOf(
    CameraFilterPreset("normal", "Original", Color.Transparent, 0f),
    CameraFilterPreset("tokyo", "Tokyo Neon", Color(0xFF00F0FF), 0.22f),
    CameraFilterPreset("violet", "Cyber Glow", Color(0xFF9D4EDD), 0.25f),
    CameraFilterPreset("noir", "Noir Contrast", Color(0xFF1E293B), 0.35f),
    CameraFilterPreset("sunset", "Golden Sunset", Color(0xFFFF5964), 0.22f)
  )
  var selectedFilter by remember { mutableStateOf(filterPresets[0]) }

  // Post-Capture Editor States
  var captionText by remember { mutableStateOf("") }
  var isCloseCircleOnly by remember { mutableStateOf(false) }
  var publishDestination by remember { mutableStateOf("Story") } // "Story" or "Feed"
  var showSmartCaptionsSheet by remember { mutableStateOf(false) }
  var isSuccessPublished by remember { mutableStateOf(false) }

  // Default camera sample visual if real live camera feed isn't active
  val defaultLivePreview = if (isFrontCamera) {
    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=900&q=80"
  } else {
    "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=900&q=80"
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VyroBlack)
      .testTag("capture_screen")
  ) {
    if (selectedMediaUri == null) {
      // MODE 1: LIVE VIEWFINDER
      Box(modifier = Modifier.fillMaxSize()) {
        // Viewfinder Image with applied Filter Tint
        AsyncImage(
          model = defaultLivePreview,
          contentDescription = "Viewfinder preview",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Filter Tint Layer
        if (selectedFilter.overlayAlpha > 0f) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(selectedFilter.tintColor.copy(alpha = selectedFilter.overlayAlpha))
          )
        }

        // Top Controls: Flash, Timer, Flip, Close
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onClose,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.5f))
              .testTag("close_capture_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Flash Toggle
            IconButton(
              onClick = { isFlashOn = !isFlashOn },
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .testTag("flash_toggle_button")
            ) {
              Icon(
                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Flash",
                tint = if (isFlashOn) accent.primary else Color.White
              )
            }

            // Timer Toggle
            IconButton(
              onClick = {
                timerSeconds = when (timerSeconds) {
                  0 -> 3
                  3 -> 10
                  else -> 0
                }
              },
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .testTag("timer_toggle_button")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Timer, contentDescription = "Timer", tint = Color.White)
                if (timerSeconds > 0) {
                  Text(
                    text = "${timerSeconds}s",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent.primary,
                    modifier = Modifier.padding(top = 1.dp)
                  )
                }
              }
            }

            // Flip Camera Toggle
            IconButton(
              onClick = { isFrontCamera = !isFrontCamera },
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .testTag("flip_camera_button")
            ) {
              Icon(Icons.Default.Cameraswitch, contentDescription = "Flip camera", tint = Color.White)
            }
          }
        }

        // Permission notice banner if camera not yet granted
        if (!hasCameraPermission) {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
            modifier = Modifier
              .align(Alignment.TopCenter)
              .padding(top = 74.dp, start = 20.dp, end = 20.dp)
              .border(1.dp, accent.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Allow camera access for live lens captures",
                fontSize = 12.sp,
                color = VyroTextPrimary,
                modifier = Modifier.weight(1f)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
              ) {
                Text("Allow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Bottom Controls: Filters, Shutter, Gallery
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Filter Chips
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 20.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(filterPresets) { filter ->
              val isSelected = selectedFilter.id == filter.id
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(if (isSelected) accent.primary else Color.Black.copy(alpha = 0.6f))
                  .border(
                    1.dp,
                    if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                    RoundedCornerShape(16.dp)
                  )
                  .clickable { selectedFilter = filter }
                  .padding(horizontal = 14.dp, vertical = 6.dp)
                  .testTag("filter_${filter.id}")
              ) {
                Text(
                  text = filter.name,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = Color.White
                )
              }
            }
          }

          // Shutter & Gallery Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 36.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Gallery Picker Button
            IconButton(
              onClick = {
                galleryLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                .testTag("gallery_picker_button")
            ) {
              Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "Device Gallery",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }

            // Central Shutter Button
            Box(
              modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(4.dp, Color.White, CircleShape)
                .padding(6.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(accent.primary, accent.secondary)
                  )
                )
                .clickable {
                  // Capture current preview
                  selectedMediaUri = defaultLivePreview
                }
                .testTag("shutter_capture_button")
            )

            // Placeholder to balance layout
            Spacer(modifier = Modifier.size(52.dp))
          }
        }
      }
    } else {
      // MODE 2: POST-CAPTURE CREATIVE STUDIO & PUBLISH
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .navigationBarsPadding()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { selectedMediaUri = null },
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(VyroDarkSurface)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Retake", tint = VyroTextPrimary)
          }

          Text(
            text = "Publish to VYRO",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = VyroTextPrimary
          )

          Spacer(modifier = Modifier.size(38.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Media Preview Card with applied filter
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = VyroDarkSurface),
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .border(1.dp, VyroBorder, RoundedCornerShape(18.dp))
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
              model = selectedMediaUri,
              contentDescription = "Captured preview",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )

            // Applied Filter tint
            if (selectedFilter.overlayAlpha > 0f) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(selectedFilter.tintColor.copy(alpha = selectedFilter.overlayAlpha))
              )
            }

            // Caption Overlay if typed
            if (captionText.isNotBlank()) {
              Box(
                modifier = Modifier
                  .align(Alignment.BottomCenter)
                  .fillMaxWidth()
                  .padding(12.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.Black.copy(alpha = 0.6f))
                  .padding(10.dp)
              ) {
                Text(
                  text = captionText,
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Caption Input Field with Smart AI button
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = captionText,
            onValueChange = { captionText = it },
            placeholder = { Text("Add caption or vibe...", color = VyroTextTertiary, fontSize = 14.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = VyroCardSurface,
              unfocusedContainerColor = VyroCardSurface,
              focusedBorderColor = accent.primary,
              unfocusedBorderColor = VyroBorder,
              focusedTextColor = VyroTextPrimary,
              unfocusedTextColor = VyroTextPrimary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("capture_caption_input")
          )

          Spacer(modifier = Modifier.width(8.dp))

          // Smart Captions Suggestion Button
          IconButton(
            onClick = { showSmartCaptionsSheet = !showSmartCaptionsSheet },
            modifier = Modifier
              .size(50.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.linearGradient(
                  listOf(accent.primary, accent.secondary)
                )
              )
              .testTag("smart_captions_button")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Captions", tint = Color.White)
          }
        }

        // Smart Captions Quick Suggestions
        AnimatedVisibility(visible = showSmartCaptionsSheet) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(VyroDarkSurface)
              .border(0.5.dp, VyroBorder, RoundedCornerShape(14.dp))
              .padding(10.dp)
          ) {
            Text(
              text = "✨ Smart Suggestions",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = accent.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            val suggestions = VyroRepository.getSmartCaptions(selectedFilter.name)
            suggestions.take(3).forEach { suggestion ->
              Text(
                text = suggestion,
                fontSize = 12.sp,
                color = VyroTextSecondary,
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    captionText = suggestion
                    showSmartCaptionsSheet = false
                  }
                  .padding(vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Destination: Story vs Feed Tabs
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          FilterChip(
            selected = publishDestination == "Story",
            onClick = { publishDestination = "Story" },
            label = { Text("⚡ Post to Story (24h)", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = accent.primary,
              selectedLabelColor = Color.White,
              containerColor = VyroDarkSurface,
              labelColor = VyroTextSecondary
            ),
            modifier = Modifier.weight(1f)
          )

          FilterChip(
            selected = publishDestination == "Feed",
            onClick = { publishDestination = "Feed" },
            label = { Text("📸 Post to Feed", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = accent.primary,
              selectedLabelColor = Color.White,
              containerColor = VyroDarkSurface,
              labelColor = VyroTextSecondary
            ),
            modifier = Modifier.weight(1f)
          )
        }

        // Close Circle Toggle
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Close Circle Only",
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              color = VyroTextPrimary
            )
            Text(
              text = "Only selected friends can view this",
              fontSize = 11.sp,
              color = VyroTextTertiary
            )
          }

          Switch(
            checked = isCloseCircleOnly,
            onCheckedChange = { isCloseCircleOnly = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = VyroGreenCloseCircle
            ),
            modifier = Modifier.testTag("close_circle_switch")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Publish Action Button
        Button(
          onClick = {
            val uri = selectedMediaUri ?: defaultLivePreview
            if (publishDestination == "Story") {
              VyroRepository.addStory(
                mediaUri = uri,
                caption = captionText,
                isCloseCircle = isCloseCircleOnly
              )
            } else {
              VyroRepository.createPost(
                mediaUri = uri,
                caption = captionText.ifBlank { "Moments from VYRO ⚡" },
                tags = listOf("#vyro", "#moment"),
                isCloseCircle = isCloseCircleOnly
              )
            }
            isSuccessPublished = true
            onMediaPublished()
          },
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isCloseCircleOnly) VyroGreenCloseCircle else accent.primary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("publish_media_button")
        ) {
          Text(
            text = if (isCloseCircleOnly) "Share to Close Circle" else "Share Now",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}
