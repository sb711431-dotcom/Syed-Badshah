package com.example.vyro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalVyroAccent
import com.example.ui.theme.VyroBorder
import com.example.ui.theme.VyroCardSurface
import com.example.ui.theme.VyroDarkSurface
import com.example.ui.theme.VyroGreenCloseCircle
import com.example.ui.theme.VyroHeartRed
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.ui.theme.VyroTextTertiary
import com.example.vyro.data.VyroRepository
import com.example.vyro.model.NotificationItem
import com.example.vyro.ui.components.VyroAvatar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsBottomSheet(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current
  val notifications by VyroRepository.notifications.collectAsState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(),
    containerColor = VyroDarkSurface,
    modifier = modifier.testTag("notifications_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Activity Signals",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = VyroTextPrimary
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          TextButton(onClick = { VyroRepository.markAllNotificationsRead() }) {
            Text("Mark read", fontSize = 12.sp, color = accent.primary)
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = VyroTextSecondary)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (notifications.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No notifications yet", color = VyroTextTertiary, fontSize = 14.sp)
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(380.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(notifications, key = { it.id }) { item ->
            NotificationCard(item = item)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
fun NotificationCard(item: NotificationItem) {
  val accent = LocalVyroAccent.current
  val timeText = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(item.timestamp))

  val icon = when (item.type) {
    "like" -> Icons.Default.Favorite to VyroHeartRed
    "story_view" -> Icons.Default.Visibility to accent.primary
    "friend_request" -> Icons.Default.PersonAdd to VyroGreenCloseCircle
    "capsule" -> Icons.Default.LockOpen to accent.secondary
    else -> Icons.Default.Visibility to accent.primary
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(if (item.isRead) VyroCardSurface else VyroCardSurface.copy(alpha = 0.95f))
      .border(
        0.5.dp,
        if (item.isRead) VyroBorder else accent.primary.copy(alpha = 0.5f),
        RoundedCornerShape(14.dp)
      )
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box {
      if (item.userAvatar.isNotBlank()) {
        VyroAvatar(avatarUrl = item.userAvatar, size = 42.dp)
      } else {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(icon.second.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon.first, contentDescription = null, tint = icon.second, modifier = Modifier.size(22.dp))
        }
      }

      Box(
        modifier = Modifier
          .size(16.dp)
          .align(Alignment.BottomEnd)
          .clip(CircleShape)
          .background(icon.second),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon.first, contentDescription = null, tint = VyroDarkSurface, modifier = Modifier.size(10.dp))
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = item.title,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = VyroTextPrimary
      )
      Text(
        text = item.message,
        fontSize = 12.sp,
        color = VyroTextSecondary,
        maxLines = 2
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = timeText,
        fontSize = 10.sp,
        color = VyroTextTertiary
      )
    }

    if (!item.isRead) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(accent.primary)
      )
    }
  }
}
