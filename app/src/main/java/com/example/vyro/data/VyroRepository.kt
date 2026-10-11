package com.example.vyro.data

import com.example.ui.theme.VyroAccentTheme
import com.example.vyro.model.Comment
import com.example.vyro.model.Conversation
import com.example.vyro.model.MemoryCapsule
import com.example.vyro.model.Message
import com.example.vyro.model.MoodSignal
import com.example.vyro.model.NotificationItem
import com.example.vyro.model.Post
import com.example.vyro.model.Story
import com.example.vyro.model.User
import com.example.vyro.model.UserStoryGroup
import com.example.vyro.model.VyroMomentPrompt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object VyroRepository {

  private val _accentTheme = MutableStateFlow(VyroAccentTheme.VIOLET)
  val accentTheme: StateFlow<VyroAccentTheme> = _accentTheme.asStateFlow()

  fun setAccentTheme(theme: VyroAccentTheme) {
    _accentTheme.value = theme
  }

  // Current Logged-in User
  private val _currentUser = MutableStateFlow(
    User(
      id = "user_me",
      username = "alex_vyro",
      displayName = "Alex Rivera",
      bio = "Creating in the dark • Digital architecture & nocturnal frequencies ⚡",
      avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
      bannerUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&q=80",
      mood = MoodSignal.PRESETS[0],
      isCloseCircle = false,
      followersCount = 1842,
      followingCount = 421,
      postsCount = 18,
      capsulesCount = 4
    )
  )
  val currentUser: StateFlow<User> = _currentUser.asStateFlow()

  private val _isAuthenticated = MutableStateFlow(true)
  val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

  // Seed Friends
  private val _friends = MutableStateFlow(
    listOf(
      User(
        id = "user_kai",
        username = "kai_zen",
        displayName = "Kai Nakamura",
        bio = "Synthesizer jams & generative 3D visual loops 🎛️",
        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
        mood = MoodSignal.PRESETS[1],
        isCloseCircle = true,
        isFriend = true,
        isOnline = true
      ),
      User(
        id = "user_luna",
        username = "luna_noir",
        displayName = "Luna Vance",
        bio = "Tokyo nights / Cyberpunk architecture photographer 📷",
        avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
        mood = MoodSignal.PRESETS[4],
        isCloseCircle = true,
        isFriend = true,
        isOnline = true
      ),
      User(
        id = "user_jordan",
        username = "jordan_tempo",
        displayName = "Jordan Miles",
        bio = "Curating frequencies • Sound design & vinyl collector 🎧",
        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&q=80",
        mood = MoodSignal.PRESETS[2],
        isCloseCircle = false,
        isFriend = true,
        isOnline = false
      ),
      User(
        id = "user_maya",
        username = "maya_craft",
        displayName = "Maya Chen",
        bio = "Interactive design lead • Coffee fanatic ☕",
        avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&q=80",
        mood = MoodSignal.PRESETS[5],
        isCloseCircle = false,
        isFriend = true,
        isOnline = true
      ),
      User(
        id = "user_elena",
        username = "elena_vibe",
        displayName = "Elena Rostova",
        bio = "Chasing dusk along the Pacific Coast 🌊",
        avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&q=80",
        mood = MoodSignal.PRESETS[0],
        isCloseCircle = true,
        isFriend = true,
        isOnline = false
      )
    )
  )
  val friends: StateFlow<List<User>> = _friends.asStateFlow()

  // Daily Moment Prompt
  private val _currentMoment = MutableStateFlow(
    VyroMomentPrompt(
      id = "moment_today",
      prompt = "Today's Pulse: Neon Twilight Vibe ⚡",
      subtitle = "Capture the ambient color around your workspace or street before the sun vanishes",
      expiresAtHours = 12,
      participantsCount = 892
    )
  )
  val currentMoment: StateFlow<VyroMomentPrompt> = _currentMoment.asStateFlow()

  // Ephemeral Stories (Enforces 24h expiration)
  private val _stories = MutableStateFlow<List<UserStoryGroup>>(emptyList())
  val stories: StateFlow<List<UserStoryGroup>> = _stories.asStateFlow()

  // Posts Feed
  private val _posts = MutableStateFlow<List<Post>>(emptyList())
  val posts: StateFlow<List<Post>> = _posts.asStateFlow()

  // Conversations
  private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
  val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

  // Memory Capsules
  private val _capsules = MutableStateFlow<List<MemoryCapsule>>(emptyList())
  val capsules: StateFlow<List<MemoryCapsule>> = _capsules.asStateFlow()

  // Notifications
  private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
  val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

  init {
    seedInitialData()
  }

  private fun seedInitialData() {
    val now = System.currentTimeMillis()

    // Stories
    val initialStories = listOf(
      UserStoryGroup(
        userId = "user_luna",
        userName = "Luna Vance",
        userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
        isCloseCircle = true,
        stories = listOf(
          Story(
            id = "story_1",
            userId = "user_luna",
            userName = "Luna Vance",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
            mediaUri = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=900&q=80",
            caption = "Midnight exploration in Shibuya 🌧️ neon reflections everywhere",
            timestamp = now - (2 * 3600 * 1000),
            isViewed = false,
            isCloseCircleOnly = true,
            viewCount = 42
          ),
          Story(
            id = "story_1b",
            userId = "user_luna",
            userName = "Luna Vance",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
            mediaUri = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=900&q=80",
            caption = "Late night rooftop ramen with the crew",
            timestamp = now - (1 * 3600 * 1000),
            isViewed = false,
            isCloseCircleOnly = true,
            viewCount = 38
          )
        )
      ),
      UserStoryGroup(
        userId = "user_kai",
        userName = "Kai Nakamura",
        userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
        isCloseCircle = true,
        stories = listOf(
          Story(
            id = "story_2",
            userId = "user_kai",
            userName = "Kai Nakamura",
            userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
            mediaUri = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=900&q=80",
            caption = "Modular synth patch running since 3 AM 🎛️ sound on",
            timestamp = now - (4 * 3600 * 1000),
            isViewed = false,
            isCloseCircleOnly = false,
            viewCount = 65
          )
        )
      ),
      UserStoryGroup(
        userId = "user_maya",
        userName = "Maya Chen",
        userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&q=80",
        isCloseCircle = false,
        stories = listOf(
          Story(
            id = "story_3",
            userId = "user_maya",
            userName = "Maya Chen",
            userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&q=80",
            mediaUri = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=900&q=80",
            caption = "Morning espresso before diving into wireframes ☕",
            timestamp = now - (6 * 3600 * 1000),
            isViewed = true,
            isCloseCircleOnly = false,
            viewCount = 89
          )
        )
      ),
      UserStoryGroup(
        userId = "user_elena",
        userName = "Elena Rostova",
        userAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&q=80",
        isCloseCircle = true,
        stories = listOf(
          Story(
            id = "story_4",
            userId = "user_elena",
            userName = "Elena Rostova",
            userAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&q=80",
            mediaUri = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&q=80",
            caption = "Golden hour horizon at Big Sur 🌅",
            timestamp = now - (8 * 3600 * 1000),
            isViewed = true,
            isCloseCircleOnly = true,
            viewCount = 112
          )
        )
      )
    )
    _stories.value = initialStories

    // Feed Posts
    _posts.value = listOf(
      Post(
        id = "post_1",
        userId = "user_luna",
        userName = "Luna Vance",
        userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
        mediaUri = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=900&q=80",
        caption = "Translucent cyberpunk alleys. Captured during a soft rain in Shinjuku. The neon violet hue feels unreal in person ✨",
        tags = listOf("#cyberpunk", "#shinjuku", "#neonvibes", "#vyro"),
        location = "Shinjuku, Tokyo",
        timestamp = now - (35 * 60 * 1000),
        likesCount = 284,
        commentsCount = 24,
        isLiked = true,
        isSaved = true,
        comments = listOf(
          Comment(
            id = "c1",
            userId = "user_kai",
            userName = "Kai Nakamura",
            userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
            text = "The reflection framing is insane Luna! What lens?",
            timestamp = now - (20 * 60 * 1000)
          ),
          Comment(
            id = "c2",
            userId = "user_me",
            userName = "Alex Rivera",
            userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
            text = "Need to visit this spot next month 🔥",
            timestamp = now - (15 * 60 * 1000)
          )
        )
      ),
      Post(
        id = "post_2",
        userId = "user_kai",
        userName = "Kai Nakamura",
        userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
        mediaUri = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=900&q=80",
        caption = "Testing the analog frequency filter on our upcoming ambient EP. Pure analog warmth through vintage tubes 🎚️",
        tags = listOf("#modular", "#synthesizer", "#ambient", "#sounddesign"),
        location = "Echo Studio 4",
        timestamp = now - (2 * 3600 * 1000),
        likesCount = 192,
        commentsCount = 14,
        isLiked = false,
        isCloseCircle = true,
        comments = listOf(
          Comment(
            id = "c3",
            userId = "user_jordan",
            userName = "Jordan Miles",
            userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&q=80",
            text = "That sub-bass frequency was shaking my monitors!",
            timestamp = now - (1 * 3600 * 1000)
          )
        )
      ),
      Post(
        id = "post_3",
        userId = "user_elena",
        userName = "Elena Rostova",
        userAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&q=80",
        mediaUri = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&q=80",
        caption = "Pacific ocean calm. Breathing in salty air and setting new creative goals for the season ahead 🌊",
        tags = listOf("#nature", "#ocean", "#coast", "#peace"),
        location = "Big Sur, California",
        timestamp = now - (5 * 3600 * 1000),
        likesCount = 412,
        commentsCount = 31,
        isLiked = false,
        comments = emptyList()
      )
    )

    // Seed Conversations
    val lunaUser = _friends.value.first { it.id == "user_luna" }
    val kaiUser = _friends.value.first { it.id == "user_kai" }
    val mayaUser = _friends.value.first { it.id == "user_maya" }
    val jordanUser = _friends.value.first { it.id == "user_jordan" }

    _conversations.value = listOf(
      Conversation(
        id = "conv_luna",
        participant = lunaUser,
        lastMessage = Message(
          id = "msg_l1",
          conversationId = "conv_luna",
          senderId = "user_luna",
          senderName = "Luna Vance",
          text = "Sending you the raw 4K stills from yesterday! Check your capsules ⚡",
          timestamp = now - (8 * 60 * 1000),
          isRead = false
        ),
        unreadCount = 1,
        isDisappearingMode = true
      ),
      Conversation(
        id = "conv_kai",
        participant = kaiUser,
        lastMessage = Message(
          id = "msg_k1",
          conversationId = "conv_kai",
          senderId = "user_me",
          senderName = "Alex Rivera",
          text = "The audio track export sounds super clean brother! 🎧",
          timestamp = now - (45 * 60 * 1000),
          isRead = true
        ),
        unreadCount = 0
      ),
      Conversation(
        id = "conv_maya",
        participant = mayaUser,
        lastMessage = Message(
          id = "msg_m1",
          conversationId = "conv_maya",
          senderId = "user_maya",
          senderName = "Maya Chen",
          text = "Are we syncing up for design critique at 4 PM?",
          timestamp = now - (3 * 3600 * 1000),
          isRead = true
        ),
        unreadCount = 0
      ),
      Conversation(
        id = "conv_jordan",
        participant = jordanUser,
        lastMessage = Message(
          id = "msg_j1",
          conversationId = "conv_jordan",
          senderId = "user_jordan",
          senderName = "Jordan Miles",
          text = "Voice note: check this vinyl sample (0:24)",
          isVoice = true,
          voiceDurationSec = 24,
          timestamp = now - (7 * 3600 * 1000),
          isRead = true
        ),
        unreadCount = 0
      )
    )

    // Memory Capsules
    _capsules.value = listOf(
      MemoryCapsule(
        id = "cap_1",
        title = "Tokyo Neon Midnight '26",
        description = "Unfiltered snapshots & voice clips recorded across Shibuya and Akihabara backstreets.",
        coverUri = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=900&q=80",
        photosCount = 18,
        unlockDate = now,
        isLocked = false,
        category = "Travel Archive",
        isSharedWithCircle = true
      ),
      MemoryCapsule(
        id = "cap_2",
        title = "Studio Acoustic Sessions",
        description = "Early demos and ambient modular synth jams before the final album release.",
        coverUri = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=900&q=80",
        photosCount = 9,
        unlockDate = now,
        isLocked = false,
        category = "Creative Vault",
        isSharedWithCircle = false
      ),
      MemoryCapsule(
        id = "cap_3",
        title = "Summer Solstice Roadtrip",
        description = "Sealed memory capsule. Unlocks on New Year's Eve 2026.",
        coverUri = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=900&q=80",
        photosCount = 24,
        unlockDate = now + (80 * 24 * 3600 * 1000L),
        isLocked = true,
        category = "Time Capsule",
        isSharedWithCircle = true
      )
    )

    // Notifications
    _notifications.value = listOf(
      NotificationItem(
        id = "n_1",
        type = "story_view",
        title = "Story Viewers",
        message = "Luna Vance and 41 others watched your Tokyo story",
        userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
        timestamp = now - (15 * 60 * 1000),
        isRead = false
      ),
      NotificationItem(
        id = "n_2",
        type = "like",
        title = "Post Liked",
        message = "Kai Nakamura liked your post 'Translucent cyberpunk alleys'",
        userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
        timestamp = now - (30 * 60 * 1000),
        isRead = false
      ),
      NotificationItem(
        id = "n_3",
        type = "friend_request",
        title = "Close Circle",
        message = "Elena Rostova added you to their Close Circle",
        userAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&q=80",
        timestamp = now - (2 * 3600 * 1000),
        isRead = true
      ),
      NotificationItem(
        id = "n_4",
        type = "capsule",
        title = "Memory Unlocked",
        message = "Capsule 'Tokyo Neon Midnight' is now unlocked and ready to view",
        userAvatar = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80",
        timestamp = now - (5 * 3600 * 1000),
        isRead = true
      )
    )
  }

  // Story Interactions
  fun addStory(mediaUri: String, caption: String, isCloseCircle: Boolean) {
    val me = _currentUser.value
    val newStory = Story(
      id = "story_${UUID.randomUUID()}",
      userId = me.id,
      userName = me.displayName,
      userAvatar = me.avatarUrl,
      mediaUri = mediaUri,
      caption = caption,
      timestamp = System.currentTimeMillis(),
      isViewed = false,
      isCloseCircleOnly = isCloseCircle,
      viewCount = 1
    )

    val currentList = _stories.value.toMutableList()
    val myGroupIndex = currentList.indexOfFirst { it.userId == me.id }
    if (myGroupIndex >= 0) {
      val existing = currentList[myGroupIndex]
      currentList[myGroupIndex] = existing.copy(
        stories = listOf(newStory) + existing.stories
      )
    } else {
      currentList.add(
        0,
        UserStoryGroup(
          userId = me.id,
          userName = me.displayName,
          userAvatar = me.avatarUrl,
          isCloseCircle = isCloseCircle,
          stories = listOf(newStory)
        )
      )
    }
    _stories.value = currentList
  }

  fun markStoryViewed(storyId: String) {
    _stories.value = _stories.value.map { group ->
      val updated = group.stories.map { story ->
        if (story.id == storyId) story.copy(isViewed = true) else story
      }
      group.copy(stories = updated)
    }
  }

  // Post Interactions
  fun toggleLikePost(postId: String) {
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) {
        val newLiked = !post.isLiked
        val count = if (newLiked) post.likesCount + 1 else post.likesCount - 1
        post.copy(isLiked = newLiked, likesCount = count.coerceAtLeast(0))
      } else post
    }
  }

  fun toggleSavePost(postId: String) {
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
    }
  }

  fun addComment(postId: String, text: String) {
    if (text.isBlank()) return
    val me = _currentUser.value
    val newComment = Comment(
      id = "c_${UUID.randomUUID()}",
      userId = me.id,
      userName = me.displayName,
      userAvatar = me.avatarUrl,
      text = text.trim(),
      timestamp = System.currentTimeMillis()
    )
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) {
        post.copy(
          comments = post.comments + newComment,
          commentsCount = post.commentsCount + 1
        )
      } else post
    }
  }

  fun createPost(mediaUri: String, caption: String, tags: List<String>, isCloseCircle: Boolean) {
    val me = _currentUser.value
    val newPost = Post(
      id = "post_${UUID.randomUUID()}",
      userId = me.id,
      userName = me.displayName,
      userAvatar = me.avatarUrl,
      mediaUri = mediaUri,
      caption = caption,
      tags = tags,
      location = "Live from VYRO",
      timestamp = System.currentTimeMillis(),
      likesCount = 0,
      commentsCount = 0,
      isLiked = false,
      isSaved = false,
      isCloseCircle = isCloseCircle,
      comments = emptyList()
    )
    _posts.value = listOf(newPost) + _posts.value
    _currentUser.value = me.copy(postsCount = me.postsCount + 1)
  }

  // Conversations & Direct Messaging
  fun getMessagesForConversation(convId: String): List<Message> {
    // Generates or returns messages
    val conv = _conversations.value.find { it.id == convId } ?: return emptyList()
    val base = mutableListOf<Message>()
    if (conv.lastMessage != null) {
      base.add(conv.lastMessage)
    }
    // Contextual chat history
    val now = System.currentTimeMillis()
    base.add(
      0,
      Message(
        id = "msg_hist_${convId}_1",
        conversationId = convId,
        senderId = conv.participant.id,
        senderName = conv.participant.displayName,
        text = "Hey Alex! Did you catch that live moment prompt earlier?",
        timestamp = now - (2 * 3600 * 1000),
        isRead = true
      )
    )
    base.add(
      1,
      Message(
        id = "msg_hist_${convId}_2",
        conversationId = convId,
        senderId = "user_me",
        senderName = "Alex Rivera",
        text = "Yes! Captured right when the neon signs came on. Looked surreal.",
        timestamp = now - (90 * 60 * 1000),
        isRead = true
      )
    )
    return base.sortedBy { it.timestamp }
  }

  fun sendMessage(
    convId: String,
    text: String,
    mediaUri: String = "",
    isVoice: Boolean = false,
    isDisappearing: Boolean = false
  ) {
    val me = _currentUser.value
    val newMsg = Message(
      id = "msg_${UUID.randomUUID()}",
      conversationId = convId,
      senderId = me.id,
      senderName = me.displayName,
      text = text,
      mediaUri = mediaUri,
      isVoice = isVoice,
      voiceDurationSec = if (isVoice) 12 else 0,
      timestamp = System.currentTimeMillis(),
      isRead = true,
      isDisappearing = isDisappearing,
      disappearingExpiresAt = if (isDisappearing) System.currentTimeMillis() + (24 * 3600 * 1000) else null
    )

    _conversations.value = _conversations.value.map { conv ->
      if (conv.id == convId) {
        conv.copy(lastMessage = newMsg, unreadCount = 0)
      } else conv
    }
  }

  fun markConversationRead(convId: String) {
    _conversations.value = _conversations.value.map { conv ->
      if (conv.id == convId) conv.copy(unreadCount = 0) else conv
    }
  }

  fun toggleDisappearingMode(convId: String) {
    _conversations.value = _conversations.value.map { conv ->
      if (conv.id == convId) conv.copy(isDisappearingMode = !conv.isDisappearingMode) else conv
    }
  }

  // Mood Signals
  fun updateMood(mood: MoodSignal) {
    _currentUser.value = _currentUser.value.copy(mood = mood)
  }

  // Profile update
  fun updateProfile(displayName: String, bio: String, avatarUrl: String) {
    _currentUser.value = _currentUser.value.copy(
      displayName = displayName.ifBlank { _currentUser.value.displayName },
      bio = bio,
      avatarUrl = avatarUrl.ifBlank { _currentUser.value.avatarUrl }
    )
  }

  // Memory Capsules
  fun createCapsule(
    title: String,
    description: String,
    coverUri: String,
    unlockDate: Long,
    isShared: Boolean
  ) {
    val newCap = MemoryCapsule(
      id = "cap_${UUID.randomUUID()}",
      title = title,
      description = description,
      coverUri = coverUri.ifBlank { "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=900&q=80" },
      photosCount = 6,
      unlockDate = unlockDate,
      isLocked = unlockDate > System.currentTimeMillis(),
      category = "Custom Archive",
      isSharedWithCircle = isShared
    )
    _capsules.value = listOf(newCap) + _capsules.value
    _currentUser.value = _currentUser.value.copy(capsulesCount = _currentUser.value.capsulesCount + 1)
  }

  fun unlockCapsule(capsuleId: String) {
    _capsules.value = _capsules.value.map { cap ->
      if (cap.id == capsuleId) cap.copy(isLocked = false) else cap
    }
  }

  // Friends & Social
  fun toggleCloseCircle(userId: String) {
    _friends.value = _friends.value.map { u ->
      if (u.id == userId) u.copy(isCloseCircle = !u.isCloseCircle) else u
    }
  }

  fun toggleFriend(userId: String) {
    _friends.value = _friends.value.map { u ->
      if (u.id == userId) u.copy(isFriend = !u.isFriend) else u
    }
  }

  fun blockUser(userId: String) {
    _friends.value = _friends.value.filterNot { it.id == userId }
    _conversations.value = _conversations.value.filterNot { it.participant.id == userId }
    _posts.value = _posts.value.filterNot { it.userId == userId }
  }

  fun reportUser(userId: String, reason: String) {
    // Log report and notify
    val notif = NotificationItem(
      id = "rep_${UUID.randomUUID()}",
      type = "report",
      title = "Report Received",
      message = "Thank you. Our safety moderation team is reviewing this account.",
      userAvatar = "",
      timestamp = System.currentTimeMillis(),
      isRead = false
    )
    _notifications.value = listOf(notif) + _notifications.value
  }

  // Notifications
  fun markAllNotificationsRead() {
    _notifications.value = _notifications.value.map { it.copy(isRead = true) }
  }

  // Smart Captions
  fun getSmartCaptions(tag: String = "general"): List<String> {
    return when (tag.lowercase()) {
      "neon", "night" -> listOf(
        "Caught in between midnight and neon dreams ⚡",
        "Nocturnal frequencies hitting just right 🌧️",
        "Electric vibrations in a quiet city ✨",
        "Subtle glow against the concrete midnight 🔮"
      )
      "moment", "chill" -> listOf(
        "Freezing this 5-second frequency in time 🌙",
        "Golden hour fading into electric dusk 🌅",
        "Pure aesthetic, zero friction ✨",
        "Living in real-time, no filters needed 🌊"
      )
      else -> listOf(
        "Signals tuned to the creative wavelength ⚡",
        "Translucent moments captured forever 📸",
        "VYRO pulse detected • Stay connected 🌌",
        "Raw aesthetic energy on repeat 🎧"
      )
    }
  }

  // Authentication
  fun login(usernameOrEmail: String) {
    _isAuthenticated.value = true
    if (usernameOrEmail.isNotBlank()) {
      _currentUser.value = _currentUser.value.copy(
        username = usernameOrEmail.removePrefix("@").split("@").first()
      )
    }
  }

  fun signup(username: String, displayName: String, email: String) {
    _isAuthenticated.value = true
    _currentUser.value = _currentUser.value.copy(
      username = username.removePrefix("@").trim(),
      displayName = displayName.trim(),
    )
  }

  fun logout() {
    _isAuthenticated.value = false
  }
}
