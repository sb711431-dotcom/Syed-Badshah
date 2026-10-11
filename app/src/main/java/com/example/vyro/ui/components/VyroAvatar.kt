package com.example.vyro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.theme.VyroCyan
import com.example.ui.theme.VyroDarkSurface
import com.example.ui.theme.VyroGreenCloseCircle
import com.example.ui.theme.VyroOnlineGreen
import com.example.ui.theme.VyroViolet

@Composable
fun VyroAvatar(
  avatarUrl: String,
  modifier: Modifier = Modifier,
  size: Dp = 48.dp,
  hasStory: Boolean = false,
  hasUnseenStory: Boolean = false,
  isCloseCircle: Boolean = false,
  isOnline: Boolean = false,
  onClick: (() -> Unit)? = null
) {
  val ringBrush = when {
    isCloseCircle && hasStory -> Brush.linearGradient(
      listOf(VyroGreenCloseCircle, Color(0xFF6EE7B7))
    )
    hasUnseenStory -> Brush.linearGradient(
      listOf(VyroViolet, VyroCyan)
    )
    hasStory -> Brush.linearGradient(
      listOf(Color(0xFF475569), Color(0xFF334155))
    )
    else -> null
  }

  val outerModifier = modifier
    .size(size)
    .testTag("avatar_${avatarUrl.hashCode()}")
    .then(
      if (ringBrush != null) {
        Modifier
          .border(2.5.dp, ringBrush, CircleShape)
          .padding(3.dp)
      } else {
        Modifier
      }
    )
    .then(
      if (onClick != null) {
        Modifier.clickable { onClick() }
      } else {
        Modifier
      }
    )

  Box(
    modifier = outerModifier,
    contentAlignment = Alignment.Center
  ) {
    if (avatarUrl.isNotBlank()) {
      AsyncImage(
        model = avatarUrl,
        contentDescription = "Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .matchParentSize()
          .clip(CircleShape)
      )
    } else {
      Box(
        modifier = Modifier
          .matchParentSize()
          .clip(CircleShape)
          .background(VyroDarkSurface),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(size * 0.5f)
        )
      }
    }

    if (isOnline) {
      Box(
        modifier = Modifier
          .size(size * 0.28f)
          .align(Alignment.BottomEnd)
          .background(VyroOnlineGreen, CircleShape)
          .border(1.5.dp, VyroDarkSurface, CircleShape)
      )
    }
  }
}
