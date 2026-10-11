package com.example.vyro.model

data class Message(
  val id: String,
  val conversationId: String,
  val senderId: String,
  val senderName: String,
  val text: String = "",
  val mediaUri: String = "",
  val isVoice: Boolean = false,
  val voiceDurationSec: Int = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = true,
  val isDisappearing: Boolean = false,
  val disappearingExpiresAt: Long? = null,
  val reaction: String? = null
)

data class Conversation(
  val id: String,
  val participant: User,
  val lastMessage: Message?,
  val unreadCount: Int = 0,
  val isMuted: Boolean = false,
  val isDisappearingMode: Boolean = false
)
