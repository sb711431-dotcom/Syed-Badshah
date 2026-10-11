package com.example.vyro.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.LocalVyroAccent
import com.example.ui.theme.VyroAccentTheme
import com.example.ui.theme.VyroBlack
import com.example.ui.theme.VyroBorder
import com.example.ui.theme.VyroCardSurface
import com.example.ui.theme.VyroDarkSurface
import com.example.ui.theme.VyroElevatedSurface
import com.example.ui.theme.VyroGreenCloseCircle
import com.example.ui.theme.VyroHeartRed
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.ui.theme.VyroTextTertiary
import com.example.vyro.data.VyroRepository
import com.example.vyro.model.MemoryCapsule
import com.example.vyro.model.MoodSignal
import com.example.vyro.model.Post
import com.example.vyro.model.User
import com.example.vyro.ui.components.VyroAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current
  val me by VyroRepository.currentUser.collectAsState()
  val posts by VyroRepository.posts.collectAsState()
  val capsules by VyroRepository.capsules.collectAsState()
  val friends by VyroRepository.friends.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Posts, 1: Capsules, 2: Close Circle
  var showMoodPicker by remember { mutableStateOf(false) }
  var showSettingsSheet by remember { mutableStateOf(false) }
  var showCreateCapsuleDialog by remember { mutableStateOf(false) }
  var showEditProfileDialog by remember { mutableStateOf(false) }

  // New Capsule form state
  var newCapsuleTitle by remember { mutableStateOf("") }
  var newCapsuleDesc by remember { mutableStateOf("") }

  // Edit profile state
  var editName by remember { mutableStateOf(me.displayName) }
  var editBio by remember { mutableStateOf(me.bio) }

  val myPosts = posts.filter { it.userId == "user_me" || it.userId.startsWith("user_") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VyroBlack)
      .testTag("profile_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Banner & Avatar Header
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        // Banner image
        AsyncImage(
          model = me.bannerUrl,
          contentDescription = "Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
        )

        // Banner gradient overlay
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, VyroBlack.copy(alpha = 0.85f))
              )
            )
        )

        // Settings gear button
        IconButton(
          onClick = { showSettingsSheet = true },
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(16.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag("settings_button")
        ) {
          Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
        }

        // Profile Avatar
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 16.dp)
        ) {
          VyroAvatar(
            avatarUrl = me.avatarUrl,
            size = 80.dp,
            hasStory = true,
            hasUnseenStory = true
          )
        }
      }
    }

    // 2. Profile Details & Bio
    item {
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
          Column {
            Text(
              text = me.displayName,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              color = VyroTextPrimary
            )
            Text(
              text = "@${me.username}",
              fontSize = 13.sp,
              color = VyroTextTertiary
            )
          }

          // Edit Profile button
          OutlinedButton(
            onClick = {
              editName = me.displayName
              editBio = me.bio
              showEditProfileDialog = true
            },
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("edit_profile_button")
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = VyroTextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Edit", fontSize = 12.sp, color = VyroTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mood Signal Pill (Interactive)
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(VyroCardSurface)
            .border(1.dp, VyroBorder, RoundedCornerShape(16.dp))
            .clickable { showMoodPicker = true }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("profile_mood_chip"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = me.mood?.emoji ?: "🎨", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Mood: ${me.mood?.label ?: "Creative"}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accent.primary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• ${me.mood?.description ?: ""}",
            fontSize = 11.sp,
            color = VyroTextTertiary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bio Text
        Text(
          text = me.bio,
          fontSize = 13.sp,
          color = VyroTextSecondary,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Stats Counters Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(VyroDarkSurface)
            .border(0.5.dp, VyroBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          ProfileStatItem(label = "Posts", count = myPosts.size.toString())
          ProfileStatItem(label = "Friends", count = friends.size.toString())
          ProfileStatItem(label = "Capsules", count = capsules.size.toString())
          ProfileStatItem(label = "Close Circle", count = friends.count { it.isCloseCircle }.toString())
        }
      }
    }

    // 3. Tab Navigation: Posts | Capsules | Close Circle
    item {
      Spacer(modifier = Modifier.height(8.dp))
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = VyroDarkSurface,
        contentColor = accent.primary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = accent.primary
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Posts", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
          selectedContentColor = accent.primary,
          unselectedContentColor = VyroTextTertiary
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Memory Capsules", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
          selectedContentColor = accent.primary,
          unselectedContentColor = VyroTextTertiary
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Close Circle", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
          selectedContentColor = accent.primary,
          unselectedContentColor = VyroTextTertiary
        )
      }
    }

    // 4. Tab Content
    when (selectedTab) {
      0 -> {
        // Posts Grid
        if (myPosts.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("No posts published yet.", color = VyroTextTertiary, fontSize = 14.sp)
            }
          }
        } else {
          items(myPosts.chunked(3)) { trio ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              trio.forEach { post ->
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(VyroElevatedSurface)
                ) {
                  AsyncImage(
                    model = post.mediaUri,
                    contentDescription = post.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                }
              }
              repeat(3 - trio.size) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }

      1 -> {
        // Memory Capsules
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Private & Timed Archives",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = VyroTextSecondary
            )
            Button(
              onClick = { showCreateCapsuleDialog = true },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp).testTag("create_capsule_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("New Capsule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        items(capsules, key = { it.id }) { capsule ->
          MemoryCapsuleCard(
            capsule = capsule,
            onUnlock = { VyroRepository.unlockCapsule(capsule.id) }
          )
        }
      }

      2 -> {
        // Close Circle Manager
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .clip(CircleShape)
                  .background(VyroGreenCloseCircle)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Close Circle Members",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VyroTextPrimary
              )
            }
            Text(
              text = "Friends who can see your private stories, moments, and shared memory capsules.",
              fontSize = 12.sp,
              color = VyroTextTertiary,
              modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
          }
        }

        items(friends) { friend ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = VyroCardSurface),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 4.dp)
              .border(0.5.dp, VyroBorder, RoundedCornerShape(14.dp))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                VyroAvatar(
                  avatarUrl = friend.avatarUrl,
                  size = 44.dp,
                  isCloseCircle = friend.isCloseCircle
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = friend.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = VyroTextPrimary
                  )
                  Text(
                    text = "@${friend.username}",
                    fontSize = 11.sp,
                    color = VyroTextTertiary
                  )
                }
              }

              Button(
                onClick = { VyroRepository.toggleCloseCircle(friend.id) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (friend.isCloseCircle) VyroGreenCloseCircle else VyroElevatedSurface,
                  contentColor = if (friend.isCloseCircle) Color.Black else VyroTextSecondary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
              ) {
                Text(
                  text = if (friend.isCloseCircle) "In Circle ✓" else "+ Add",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }

  // Mood Selector Modal
  if (showMoodPicker) {
    ModalBottomSheet(
      onDismissRequest = { showMoodPicker = false },
      sheetState = rememberModalBottomSheetState(),
      containerColor = VyroDarkSurface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
          .testTag("mood_selector_sheet")
      ) {
        Text(
          text = "Choose Your Temporary Mood Signal",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = VyroTextPrimary
        )
        Text(
          text = "Visible on your profile and story indicators",
          fontSize = 12.sp,
          color = VyroTextTertiary
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoodSignal.PRESETS.forEach { mood ->
          val isSelected = me.mood?.id == mood.id
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) accent.primary.copy(alpha = 0.2f) else VyroCardSurface)
              .border(1.dp, if (isSelected) accent.primary else VyroBorder, RoundedCornerShape(14.dp))
              .clickable {
                VyroRepository.updateMood(mood)
                showMoodPicker = false
              }
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = mood.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = mood.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = VyroTextPrimary
              )
              Text(
                text = mood.description,
                fontSize = 12.sp,
                color = VyroTextSecondary
              )
            }
            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, tint = accent.primary)
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }

  // Create Capsule Dialog
  if (showCreateCapsuleDialog) {
    AlertDialog(
      onDismissRequest = { showCreateCapsuleDialog = false },
      title = { Text("New Memory Capsule", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Seal your private photos and memories into an archive.", fontSize = 12.sp, color = VyroTextSecondary)
          OutlinedTextField(
            value = newCapsuleTitle,
            onValueChange = { newCapsuleTitle = it },
            placeholder = { Text("Capsule Title (e.g. Kyoto Trip '26)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("capsule_title_input")
          )
          OutlinedTextField(
            value = newCapsuleDesc,
            onValueChange = { newCapsuleDesc = it },
            placeholder = { Text("Short description or note") },
            modifier = Modifier.fillMaxWidth().testTag("capsule_desc_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newCapsuleTitle.isNotBlank()) {
              VyroRepository.createCapsule(
                title = newCapsuleTitle.trim(),
                description = newCapsuleDesc.trim(),
                coverUri = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=900&q=80",
                unlockDate = System.currentTimeMillis() + (30 * 24 * 3600 * 1000L),
                isShared = true
              )
              newCapsuleTitle = ""
              newCapsuleDesc = ""
              showCreateCapsuleDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
          modifier = Modifier.testTag("save_capsule_button")
        ) {
          Text("Create")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showCreateCapsuleDialog = false }) {
          Text("Cancel")
        }
      },
      containerColor = VyroDarkSurface,
      textContentColor = VyroTextPrimary
    )
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Display Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            VyroRepository.updateProfile(editName, editBio, me.avatarUrl)
            showEditProfileDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      },
      containerColor = VyroDarkSurface,
      textContentColor = VyroTextPrimary
    )
  }

  // Settings Bottom Sheet
  if (showSettingsSheet) {
    ModalBottomSheet(
      onDismissRequest = { showSettingsSheet = false },
      sheetState = rememberModalBottomSheetState(),
      containerColor = VyroDarkSurface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
          .testTag("settings_bottom_sheet")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Settings & Customization",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = VyroTextPrimary
          )
          IconButton(onClick = { showSettingsSheet = false }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = VyroTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accent Theme Selector
        Text(
          text = "Custom Accent Theme",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = VyroTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          VyroAccentTheme.entries.forEach { themeOption ->
            val isSelected = accent == themeOption
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) themeOption.primary.copy(alpha = 0.25f) else VyroCardSurface)
                .border(
                  2.dp,
                  if (isSelected) themeOption.primary else VyroBorder,
                  RoundedCornerShape(12.dp)
                )
                .clickable {
                  VyroRepository.setAccentTheme(themeOption)
                }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(themeOption.primary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = themeOption.label.split(" ").last(),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSelected) VyroTextPrimary else VyroTextTertiary
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy Controls
        Text(
          text = "Privacy Controls",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = VyroTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        var ghostMode by remember { mutableStateOf(false) }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Ghost Mode", fontSize = 14.sp, color = VyroTextPrimary)
            Text("Hide online activity and read receipts", fontSize = 11.sp, color = VyroTextTertiary)
          }
          Switch(
            checked = ghostMode,
            onCheckedChange = { ghostMode = it },
            colors = SwitchDefaults.colors(checkedTrackColor = accent.primary)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout Button
        Button(
          onClick = {
            showSettingsSheet = false
            VyroRepository.logout()
            onLogout()
          },
          colors = ButtonDefaults.buttonColors(containerColor = VyroHeartRed.copy(alpha = 0.2f)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VyroHeartRed, RoundedCornerShape(14.dp))
            .testTag("logout_button")
        ) {
          Icon(Icons.Default.Logout, contentDescription = null, tint = VyroHeartRed, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Sign Out of VYRO", color = VyroHeartRed, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun ProfileStatItem(label: String, count: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = count,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = VyroTextPrimary
    )
    Text(
      text = label,
      fontSize = 11.sp,
      color = VyroTextTertiary
    )
  }
}

@Composable
fun MemoryCapsuleCard(
  capsule: MemoryCapsule,
  onUnlock: () -> Unit
) {
  val accent = LocalVyroAccent.current

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = VyroCardSurface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .border(0.5.dp, VyroBorder, RoundedCornerShape(16.dp))
      .testTag("memory_capsule_${capsule.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(VyroDarkSurface)
      ) {
        AsyncImage(
          model = capsule.coverUri,
          contentDescription = capsule.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        if (capsule.isLocked) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White)
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = capsule.title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = VyroTextPrimary
          )
          if (capsule.isSharedWithCircle) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(VyroGreenCloseCircle.copy(alpha = 0.2f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text("Circle", fontSize = 9.sp, color = VyroGreenCloseCircle, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = capsule.description,
          fontSize = 12.sp,
          color = VyroTextSecondary,
          maxLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "${capsule.photosCount} memories • ${capsule.category}",
          fontSize = 10.sp,
          color = VyroTextTertiary
        )
      }

      if (capsule.isLocked) {
        Button(
          onClick = onUnlock,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = VyroElevatedSurface),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(30.dp)
        ) {
          Text("Unlock", fontSize = 11.sp, color = accent.primary)
        }
      }
    }
  }
}
