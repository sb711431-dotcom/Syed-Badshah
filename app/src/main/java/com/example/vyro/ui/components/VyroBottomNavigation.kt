package com.example.vyro.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalVyroAccent
import com.example.ui.theme.VyroBorder
import com.example.ui.theme.VyroDarkSurface
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.ui.theme.VyroTextTertiary

enum class VyroNavTab {
  HOME,
  DISCOVER,
  CAPTURE,
  MESSAGES,
  PROFILE
}

@Composable
fun VyroBottomNavigation(
  currentTab: VyroNavTab,
  onSelectTab: (VyroNavTab) -> Unit,
  unreadMessagesCount: Int,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(VyroDarkSurface)
      .border(0.5.dp, VyroBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
      .navigationBarsPadding()
      .height(64.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Home
      NavTabItem(
        label = "Home",
        selected = currentTab == VyroNavTab.HOME,
        activeIcon = Icons.Filled.Home,
        inactiveIcon = Icons.Outlined.Home,
        testTag = "nav_tab_home",
        onClick = { onSelectTab(VyroNavTab.HOME) }
      )

      // 2. Discover
      NavTabItem(
        label = "Discover",
        selected = currentTab == VyroNavTab.DISCOVER,
        activeIcon = Icons.Filled.Explore,
        inactiveIcon = Icons.Outlined.Explore,
        testTag = "nav_tab_discover",
        onClick = { onSelectTab(VyroNavTab.DISCOVER) }
      )

      // 3. Capture (Central Highlighted Button)
      Box(
        modifier = Modifier
          .offset(y = (-10).dp)
          .size(54.dp)
          .shadow(8.dp, CircleShape)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              listOf(accent.primary, accent.secondary)
            )
          )
          .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) { onSelectTab(VyroNavTab.CAPTURE) }
          .testTag("nav_tab_capture"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = "Capture",
          tint = Color.White,
          modifier = Modifier.size(28.dp)
        )
      }

      // 4. Messages
      NavTabItem(
        label = "Messages",
        selected = currentTab == VyroNavTab.MESSAGES,
        activeIcon = Icons.Filled.ChatBubble,
        inactiveIcon = Icons.Filled.ChatBubbleOutline,
        badgeCount = unreadMessagesCount,
        testTag = "nav_tab_messages",
        onClick = { onSelectTab(VyroNavTab.MESSAGES) }
      )

      // 5. Profile
      NavTabItem(
        label = "Profile",
        selected = currentTab == VyroNavTab.PROFILE,
        activeIcon = Icons.Filled.Person,
        inactiveIcon = Icons.Outlined.Person,
        testTag = "nav_tab_profile",
        onClick = { onSelectTab(VyroNavTab.PROFILE) }
      )
    }
  }
}

@Composable
private fun NavTabItem(
  label: String,
  selected: Boolean,
  activeIcon: ImageVector,
  inactiveIcon: ImageVector,
  badgeCount: Int = 0,
  testTag: String,
  onClick: () -> Unit
) {
  val accent = LocalVyroAccent.current
  val iconColor by animateColorAsState(
    targetValue = if (selected) accent.primary else VyroTextTertiary,
    label = "tab_color"
  )

  Column(
    modifier = Modifier
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    BadgedBox(
      badge = {
        if (badgeCount > 0) {
          Badge(
            containerColor = accent.primary,
            contentColor = Color.White
          ) {
            Text(
              text = if (badgeCount > 9) "9+" else badgeCount.toString(),
              fontSize = 9.sp
            )
          }
        }
      }
    ) {
      Icon(
        imageVector = if (selected) activeIcon else inactiveIcon,
        contentDescription = label,
        tint = iconColor,
        modifier = Modifier.size(24.dp)
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
      color = if (selected) VyroTextPrimary else VyroTextTertiary
    )
  }
}
