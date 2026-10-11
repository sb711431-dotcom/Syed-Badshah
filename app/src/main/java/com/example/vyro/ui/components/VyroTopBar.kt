package com.example.vyro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalVyroAccent
import com.example.ui.theme.VyroBlack
import com.example.ui.theme.VyroBorder
import com.example.ui.theme.VyroCardSurface
import com.example.ui.theme.VyroHeartRed
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.vyro.model.MoodSignal

@Composable
fun VyroTopBar(
  currentMood: MoodSignal?,
  unreadNotificationsCount: Int,
  unreadMessagesCount: Int,
  onMoodClick: () -> Unit,
  onNotificationsClick: () -> Unit,
  onMessagesClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(VyroBlack.copy(alpha = 0.95f))
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Brand Logo: VYRO
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.testTag("brand_logo")
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(
            Brush.linearGradient(
              listOf(accent.primary, accent.secondary)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "V",
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          color = Color.White
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "VYRO",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        letterSpacing = 2.sp,
        color = VyroTextPrimary,
        fontFamily = FontFamily.SansSerif
      )
    }

    // Mood Signal Pill & Quick Action Icons
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Mood Pill
      if (currentMood != null) {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(VyroCardSurface)
            .border(1.dp, VyroBorder, RoundedCornerShape(16.dp))
            .clickable { onMoodClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("mood_pill_button"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = currentMood.emoji, fontSize = 14.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = currentMood.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = VyroTextSecondary
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
      }

      // Notifications Bell
      IconButton(
        onClick = onNotificationsClick,
        modifier = Modifier
          .size(40.dp)
          .testTag("notifications_button")
      ) {
        BadgedBox(
          badge = {
            if (unreadNotificationsCount > 0) {
              Badge(
                containerColor = VyroHeartRed,
                contentColor = Color.White
              ) {
                Text(
                  text = if (unreadNotificationsCount > 9) "9+" else unreadNotificationsCount.toString(),
                  fontSize = 10.sp
                )
              }
            }
          }
        ) {
          Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = "Notifications",
            tint = VyroTextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      // Quick Chat Button
      IconButton(
        onClick = onMessagesClick,
        modifier = Modifier
          .size(40.dp)
          .testTag("direct_messages_quick_button")
      ) {
        BadgedBox(
          badge = {
            if (unreadMessagesCount > 0) {
              Badge(
                containerColor = accent.primary,
                contentColor = Color.White
              ) {
                Text(
                  text = unreadMessagesCount.toString(),
                  fontSize = 10.sp
                )
              }
            }
          }
        ) {
          Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = "Messages",
            tint = VyroTextPrimary,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}
