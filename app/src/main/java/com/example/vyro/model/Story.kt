package com.example.vyro.model

data class Story(
  val id: String,
  val userId: String,
  val userName: String,
  val userAvatar: String,
  val mediaUri: String,
  val caption: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val isViewed: Boolean = false,
  val isCloseCircleOnly: Boolean = false,
  val viewCount: Int = 18,
) {
  val isExpired: Boolean
    get() = (System.currentTimeMillis() - timestamp) > 24 * 60 * 60 * 1000L
}

data class UserStoryGroup(
  val userId: String,
  val userName: String,
  val userAvatar: String,
  val isCloseCircle: Boolean,
  val stories: List<Story>
) {
  val hasUnseen: Boolean
    get() = stories.any { !it.isViewed }
}
