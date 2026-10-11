package com.example

import com.example.vyro.data.VyroRepository
import com.example.vyro.model.MoodSignal
import com.example.vyro.model.Story
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testStoryExpirationLogic() {
    val now = System.currentTimeMillis()
    val freshStory = Story(
      id = "s_fresh",
      userId = "u1",
      userName = "User",
      userAvatar = "",
      mediaUri = "",
      timestamp = now - (2 * 3600 * 1000)
    )
    assertFalse(freshStory.isExpired)

    val expiredStory = Story(
      id = "s_old",
      userId = "u1",
      userName = "User",
      userAvatar = "",
      mediaUri = "",
      timestamp = now - (26 * 3600 * 1000)
    )
    assertTrue(expiredStory.isExpired)
  }

  @Test
  fun testRepositorySocialInteractions() {
    val initialPosts = VyroRepository.posts.value
    assertTrue(initialPosts.isNotEmpty())

    val firstPost = initialPosts.first()
    val initialLikes = firstPost.likesCount
    val initialIsLiked = firstPost.isLiked

    VyroRepository.toggleLikePost(firstPost.id)
    val updatedPost = VyroRepository.posts.value.first { it.id == firstPost.id }
    assertEquals(!initialIsLiked, updatedPost.isLiked)

    // Add comment
    VyroRepository.addComment(firstPost.id, "Testing comment interaction")
    val commentedPost = VyroRepository.posts.value.first { it.id == firstPost.id }
    assertTrue(commentedPost.comments.any { it.text == "Testing comment interaction" })
  }

  @Test
  fun testMoodSignalUpdate() {
    val mood = MoodSignal.PRESETS[3] // Locked In
    VyroRepository.updateMood(mood)
    assertEquals("focus", VyroRepository.currentUser.value.mood?.id)
  }

  @Test
  fun testMemoryCapsuleCreation() {
    val initialCount = VyroRepository.capsules.value.size
    VyroRepository.createCapsule(
      title = "Test Trip 2026",
      description = "Private memory vault",
      coverUri = "",
      unlockDate = System.currentTimeMillis() + 100000,
      isShared = true
    )
    assertEquals(initialCount + 1, VyroRepository.capsules.value.size)
  }
}
