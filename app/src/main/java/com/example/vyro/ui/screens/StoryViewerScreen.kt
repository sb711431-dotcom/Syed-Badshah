package com.example.vyro.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.LocalVyroAccent
import com.example.ui.theme.VyroBlack
import com.example.ui.theme.VyroBorder
import com.example.ui.theme.VyroGreenCloseCircle
import com.example.ui.theme.VyroHeartRed
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.vyro.data.VyroRepository
import com.example.vyro.model.UserStoryGroup
import com.example.vyro.ui.components.VyroAvatar
import kotlinx.coroutines.delay

@Composable
fun StoryViewerScreen(
  storyGroup: UserStoryGroup,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current
  var currentIndex by remember { mutableIntStateOf(0) }
  var isPaused by remember { mutableStateOf(false) }
  var isLiked by remember { mutableStateOf(false) }
  var replyText by remember { mutableStateOf("") }

  val stories = storyGroup.stories
  val currentStory = stories.getOrNull(currentIndex) ?: run {
    onDismiss()
    return
  }

  // Progress animation
  val progress = remember(currentIndex) { Animatable(0f) }

  LaunchedEffect(currentIndex, isPaused) {
    VyroRepository.markStoryViewed(currentStory.id)
    if (!isPaused) {
      val remainingFraction = 1f - progress.value
      val durationMs = (remainingFraction * 5000).toInt()
      if (durationMs > 0) {
        progress.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
        // Advance to next story
        if (currentIndex < stories.size - 1) {
          currentIndex++
        } else {
          onDismiss()
        }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VyroBlack)
      .testTag("story_viewer_screen")
  ) {
    // 1. Fullscreen Image
    AsyncImage(
      model = currentStory.mediaUri,
      contentDescription = "Story media",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Dark gradient overlays for readability
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .align(Alignment.TopCenter)
        .background(
          Brush.verticalGradient(
            listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
          )
        )
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .align(Alignment.BottomCenter)
        .background(
          Brush.verticalGradient(
            listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
          )
        )
    )

    // Touch tap areas: Tap Left = Back, Tap Right = Next, Hold = Pause
    Row(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(currentIndex) {
          detectTapGestures(
            onPress = {
              isPaused = true
              tryAwaitRelease()
              isPaused = false
            },
            onTap = { offset ->
              if (offset.x < size.width * 0.35f) {
                // Back
                if (currentIndex > 0) {
                  currentIndex--
                }
              } else {
                // Forward
                if (currentIndex < stories.size - 1) {
                  currentIndex++
                } else {
                  onDismiss()
                }
              }
            }
          )
        }
    ) {
      Spacer(modifier = Modifier.weight(1f))
      Spacer(modifier = Modifier.weight(1f))
    }

    // Top Controls
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
      // Segmented Progress Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        stories.forEachIndexed { index, _ ->
          val segmentProgress = when {
            index < currentIndex -> 1f
            index == currentIndex -> progress.value
            else -> 0f
          }
          LinearProgressIndicator(
            progress = { segmentProgress },
            modifier = Modifier
              .weight(1f)
              .height(3.dp)
              .clip(RoundedCornerShape(2.dp)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.25f)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Author Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        VyroAvatar(
          avatarUrl = storyGroup.userAvatar,
          size = 38.dp,
          isCloseCircle = storyGroup.isCloseCircle
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = storyGroup.userName,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color.White
            )
            if (currentStory.isCloseCircleOnly) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(VyroGreenCloseCircle)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "Close Circle",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
              }
            }
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Visibility,
              contentDescription = null,
              tint = Color.White.copy(alpha = 0.7f),
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${currentStory.viewCount} views",
              fontSize = 11.sp,
              color = Color.White.copy(alpha = 0.7f)
            )
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_story_button")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
          )
        }
      }
    }

    // Caption (if any)
    if (currentStory.caption.isNotBlank()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.Black.copy(alpha = 0.55f))
          .padding(horizontal = 14.dp, vertical = 8.dp)
      ) {
        Text(
          text = currentStory.caption,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Bottom Reply Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = replyText,
        onValueChange = { replyText = it },
        placeholder = { Text("Send reply to ${storyGroup.userName}...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color.Black.copy(alpha = 0.6f),
          unfocusedContainerColor = Color.Black.copy(alpha = 0.6f),
          focusedBorderColor = accent.primary,
          unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
          .weight(1f)
          .testTag("story_reply_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.width(8.dp))

      // Like Reaction
      IconButton(
        onClick = { isLiked = !isLiked },
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.6f))
          .testTag("story_like_button")
      ) {
        Icon(
          imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
          contentDescription = "Like",
          tint = if (isLiked) VyroHeartRed else Color.White,
          modifier = Modifier.size(24.dp)
        )
      }

      if (replyText.isNotBlank()) {
        Spacer(modifier = Modifier.width(6.dp))
        IconButton(
          onClick = {
            VyroRepository.sendMessage(
              convId = "conv_${storyGroup.userId}",
              text = "Replying to story: \"$replyText\""
            )
            replyText = ""
          },
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(accent.primary)
            .testTag("story_send_reply_button")
        ) {
          Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}
