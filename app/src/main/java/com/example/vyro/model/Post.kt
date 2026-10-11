package com.example.vyro.model

data class Comment(
  val id: String,
  val userId: String,
  val userName: String,
  val userAvatar: String,
  val text: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class Post(
  val id: String,
  val userId: String,
  val userName: String,
  val userAvatar: String,
  val mediaUri: String,
  val caption: String,
  val tags: List<String> = emptyList(),
  val location: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val likesCount: Int = 42,
  val commentsCount: Int = 3,
  val isLiked: Boolean = false,
  val isSaved: Boolean = false,
  val isCloseCircle: Boolean = false,
  val comments: List<Comment> = emptyList()
)
