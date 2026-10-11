package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VyroBlack
import com.example.vyro.data.VyroRepository
import com.example.vyro.model.Conversation
import com.example.vyro.model.UserStoryGroup
import com.example.vyro.ui.components.VyroBottomNavigation
import com.example.vyro.ui.components.VyroNavTab
import com.example.vyro.ui.components.VyroTopBar
import com.example.vyro.ui.screens.AuthScreen
import com.example.vyro.ui.screens.CaptureScreen
import com.example.vyro.ui.screens.ChatDetailScreen
import com.example.vyro.ui.screens.DiscoverScreen
import com.example.vyro.ui.screens.HomeScreen
import com.example.vyro.ui.screens.MessagesScreen
import com.example.vyro.ui.screens.NotificationsBottomSheet
import com.example.vyro.ui.screens.ProfileScreen
import com.example.vyro.ui.screens.StoryViewerScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val accentTheme by VyroRepository.accentTheme.collectAsState()
      val isAuthenticated by VyroRepository.isAuthenticated.collectAsState()

      MyApplicationTheme(accentTheme = accentTheme) {
        if (!isAuthenticated) {
          AuthScreen(
            onAuthSuccess = { /* state handled in repository */ }
          )
        } else {
          VyroMainApp()
        }
      }
    }
  }
}

@Composable
fun VyroMainApp() {
  var currentTab by remember { mutableStateOf(VyroNavTab.HOME) }
  var selectedConversation by remember { mutableStateOf<Conversation?>(null) }
  var activeStoryGroup by remember { mutableStateOf<UserStoryGroup?>(null) }
  var showNotificationsSheet by remember { mutableStateOf(false) }

  val currentUser by VyroRepository.currentUser.collectAsState()
  val notifications by VyroRepository.notifications.collectAsState()
  val conversations by VyroRepository.conversations.collectAsState()

  val unreadNotificationsCount = notifications.count { !it.isRead }
  val unreadMessagesCount = conversations.sumOf { it.unreadCount }

  // 1. Fullscreen Story Viewer (Takes over screen when active)
  if (activeStoryGroup != null) {
    StoryViewerScreen(
      storyGroup = activeStoryGroup!!,
      onDismiss = { activeStoryGroup = null }
    )
    return
  }

  // 2. Chat Detail Screen (Takes over screen when active conversation)
  if (selectedConversation != null) {
    ChatDetailScreen(
      conversation = selectedConversation!!,
      onBack = { selectedConversation = null }
    )
    return
  }

  // 3. Main Navigation Shell with TopBar and BottomNavigation
  Scaffold(
    topBar = {
      // Don't show standard TopBar on Capture tab for immersive camera experience
      if (currentTab != VyroNavTab.CAPTURE) {
        VyroTopBar(
          currentMood = currentUser.mood,
          unreadNotificationsCount = unreadNotificationsCount,
          unreadMessagesCount = unreadMessagesCount,
          onMoodClick = { currentTab = VyroNavTab.PROFILE },
          onNotificationsClick = { showNotificationsSheet = true },
          onMessagesClick = { currentTab = VyroNavTab.MESSAGES }
        )
      }
    },
    bottomBar = {
      VyroBottomNavigation(
        currentTab = currentTab,
        onSelectTab = { selectedTab -> currentTab = selectedTab },
        unreadMessagesCount = unreadMessagesCount
      )
    },
    containerColor = VyroBlack,
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(VyroBlack)
        .padding(
          top = if (currentTab == VyroNavTab.CAPTURE) 0.dp else innerPadding.calculateTopPadding(),
          bottom = innerPadding.calculateBottomPadding()
        )
    ) {
      AnimatedContent(
        targetState = currentTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "tab_navigation"
      ) { tab ->
        when (tab) {
          VyroNavTab.HOME -> {
            HomeScreen(
              onOpenStory = { storyGroup -> activeStoryGroup = storyGroup },
              onNavigateToCapture = { currentTab = VyroNavTab.CAPTURE },
              onNavigateToMessages = { currentTab = VyroNavTab.MESSAGES }
            )
          }

          VyroNavTab.DISCOVER -> {
            DiscoverScreen(
              onSelectPost = { /* opened in discovery */ }
            )
          }

          VyroNavTab.CAPTURE -> {
            CaptureScreen(
              onMediaPublished = { currentTab = VyroNavTab.HOME },
              onClose = { currentTab = VyroNavTab.HOME }
            )
          }

          VyroNavTab.MESSAGES -> {
            MessagesScreen(
              onOpenConversation = { conv -> selectedConversation = conv }
            )
          }

          VyroNavTab.PROFILE -> {
            ProfileScreen(
              onLogout = { /* auth state handled */ }
            )
          }
        }
      }
    }
  }

  // Notifications Bottom Sheet
  if (showNotificationsSheet) {
    NotificationsBottomSheet(
      onDismiss = { showNotificationsSheet = false }
    )
  }
}
