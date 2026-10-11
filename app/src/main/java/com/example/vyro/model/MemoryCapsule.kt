package com.example.vyro.model

data class MemoryCapsule(
  val id: String,
  val title: String,
  val description: String,
  val coverUri: String,
  val photosCount: Int = 5,
  val unlockDate: Long = System.currentTimeMillis(),
  val isLocked: Boolean = false,
  val category: String = "Midnight Archive",
  val isSharedWithCircle: Boolean = false
)

data class NotificationItem(
  val id: String,
  val type: String, // "like", "story_view", "message", "friend_request", "capsule"
  val title: String,
  val message: String,
  val userAvatar: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false,
  val actionData: String? = null
)

data class VyroMomentPrompt(
  val id: String,
  val prompt: String,
  val subtitle: String,
  val expiresAtHours: Int = 14,
  val participantsCount: Int = 412
)
