package com.example.vyro.model

data class MoodSignal(
  val id: String,
  val emoji: String,
  val label: String,
  val description: String,
  val colorHex: Long,
) {
  companion object {
    val PRESETS = listOf(
      MoodSignal("creative", "🎨", "Creative", "In deep flow state", 0xFF9D4EDD),
      MoodSignal("chill", "🌙", "Chill", "Listening to lo-fi", 0xFF00F0FF),
      MoodSignal("happy", "⚡", "Hyped", "Energized & ready", 0xFFFF5964),
      MoodSignal("focus", "🎯", "Locked In", "Do not disturb", 0xFF10B981),
      MoodSignal("owl", "🦉", "Night Owl", "Late night wanderings", 0xFF818CF8),
      MoodSignal("coffee", "☕", "Café Mode", "Grinding at the shop", 0xFFF59E0B)
    )
  }
}

data class User(
  val id: String,
  val username: String,
  val displayName: String,
  val bio: String,
  val avatarUrl: String,
  val bannerUrl: String = "",
  val mood: MoodSignal? = MoodSignal.PRESETS[0],
  val isCloseCircle: Boolean = false,
  val isFriend: Boolean = true,
  val isOnline: Boolean = true,
  val followersCount: Int = 1240,
  val followingCount: Int = 380,
  val postsCount: Int = 24,
  val capsulesCount: Int = 4,
)
