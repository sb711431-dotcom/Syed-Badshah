package com.example.vyro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.ui.theme.VyroCardSurface
import com.example.ui.theme.VyroDarkSurface
import com.example.ui.theme.VyroElevatedSurface
import com.example.ui.theme.VyroGreenCloseCircle
import com.example.ui.theme.VyroHeartRed
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.ui.theme.VyroTextTertiary
import com.example.vyro.data.VyroRepository
import com.example.vyro.model.Comment
import com.example.vyro.model.Post
import com.example.vyro.model.UserStoryGroup
import com.example.vyro.model.VyroMomentPrompt
import com.example.vyro.ui.components.VyroAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onOpenStory: (UserStoryGroup) -> Unit,
  onNavigateToCapture: () -> Unit,
  onNavigateToMessages: () -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current
  val stories by VyroRepository.stories.collectAsState()
  val posts by VyroRepository.posts.collectAsState()
  val moment by VyroRepository.currentMoment.collectAsState()
  val me by VyroRepository.currentUser.collectAsState()

  var selectedPostForComments by remember { mutableStateOf<Post?>(null) }
  var commentInputText by remember { mutableStateOf("") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VyroBlack)
      .testTag("home_feed_list"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Stories Carousel Tray
    item {
      StoriesTray(
        stories = stories,
        myAvatar = me.avatarUrl,
        onAddStory = onNavigateToCapture,
        onSelectStory = onOpenStory
      )
    }

    // 2. Daily VYRO Moment Prompt Card
    item {
      VyroMomentCard(
        moment = moment,
        onParticipate = onNavigateToCapture,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
      )
    }

    // 3. Feed Posts
    items(posts, key = { it.id }) { post ->
      PostCard(
        post = post,
        onLike = { VyroRepository.toggleLikePost(post.id) },
        onSave = { VyroRepository.toggleSavePost(post.id) },
        onCommentClick = { selectedPostForComments = post },
        onShare = { /* share handled in UI */ },
        onReport = { VyroRepository.reportUser(post.userId, "Inappropriate content") }
      )
    }
  }

  // Comments Bottom Sheet
  if (selectedPostForComments != null) {
    val post = selectedPostForComments!!
    val currentPost = posts.find { it.id == post.id } ?: post

    ModalBottomSheet(
      onDismissRequest = {
        selectedPostForComments = null
        commentInputText = ""
      },
      sheetState = rememberModalBottomSheetState(),
      containerColor = VyroDarkSurface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .testTag("comments_bottom_sheet")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Comments (${currentPost.comments.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = VyroTextPrimary
          )
          IconButton(onClick = { selectedPostForComments = null }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = VyroTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Comments list
        if (currentPost.comments.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No comments yet. Start the conversation!",
              color = VyroTextTertiary,
              fontSize = 14.sp
            )
          }
        } else {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            currentPost.comments.forEach { comment ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
              ) {
                VyroAvatar(
                  avatarUrl = comment.userAvatar,
                  size = 32.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = comment.userName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = VyroTextPrimary
                  )
                  Text(
                    text = comment.text,
                    fontSize = 14.sp,
                    color = VyroTextSecondary
                  )
                }
              }
            }
          }
        }

        // Add Comment Input
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = commentInputText,
            onValueChange = { commentInputText = it },
            placeholder = { Text("Add a comment...", color = VyroTextTertiary, fontSize = 14.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = VyroElevatedSurface,
              unfocusedContainerColor = VyroElevatedSurface,
              focusedBorderColor = accent.primary,
              unfocusedBorderColor = VyroBorder,
              focusedTextColor = VyroTextPrimary,
              unfocusedTextColor = VyroTextPrimary
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("comment_input_field"),
            singleLine = true
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = {
              if (commentInputText.isNotBlank()) {
                VyroRepository.addComment(currentPost.id, commentInputText)
                commentInputText = ""
              }
            },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(accent.primary)
              .testTag("submit_comment_button")
          ) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}

@Composable
fun StoriesTray(
  stories: List<UserStoryGroup>,
  myAvatar: String,
  onAddStory: () -> Unit,
  onSelectStory: (UserStoryGroup) -> Unit
) {
  val accent = LocalVyroAccent.current

  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp)
      .testTag("stories_tray"),
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. My Story / Add Story button
    item {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clickable { onAddStory() }
          .testTag("add_story_item")
      ) {
        Box(
          modifier = Modifier.size(64.dp),
          contentAlignment = Alignment.Center
        ) {
          VyroAvatar(
            avatarUrl = myAvatar,
            size = 64.dp
          )
          Box(
            modifier = Modifier
              .size(24.dp)
              .align(Alignment.BottomEnd)
              .clip(CircleShape)
              .background(accent.primary)
              .border(2.dp, VyroBlack, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add Story",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Your Story",
          fontSize = 11.sp,
          color = VyroTextSecondary,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // 2. Friends Stories
    items(stories, key = { it.userId }) { group ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clickable { onSelectStory(group) }
          .testTag("story_item_${group.userId}")
      ) {
        VyroAvatar(
          avatarUrl = group.userAvatar,
          size = 64.dp,
          hasStory = true,
          hasUnseenStory = group.hasUnseen,
          isCloseCircle = group.isCloseCircle
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = group.userName.split(" ").first(),
          fontSize = 11.sp,
          color = if (group.hasUnseen) VyroTextPrimary else VyroTextTertiary,
          fontWeight = if (group.hasUnseen) FontWeight.SemiBold else FontWeight.Normal,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
fun VyroMomentCard(
  moment: VyroMomentPrompt,
  onParticipate: () -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = VyroCardSurface),
    modifier = modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        brush = Brush.horizontalGradient(
          listOf(accent.primary.copy(alpha = 0.6f), accent.secondary.copy(alpha = 0.6f))
        ),
        shape = RoundedCornerShape(16.dp)
      )
      .testTag("vyro_moment_card")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(
            Brush.linearGradient(
              listOf(accent.primary, accent.secondary)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.FlashOn,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(26.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = moment.prompt,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = VyroTextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${moment.participantsCount} friends captured • ${moment.expiresAtHours}h left",
          fontSize = 11.sp,
          color = VyroTextSecondary
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = onParticipate,
        colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        modifier = Modifier.testTag("capture_moment_button")
      ) {
        Text(text = "Capture", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun PostCard(
  post: Post,
  onLike: () -> Unit,
  onSave: () -> Unit,
  onCommentClick: () -> Unit,
  onShare: () -> Unit,
  onReport: () -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current
  var showMenu by remember { mutableStateOf(false) }
  var showHeartAnimation by remember { mutableStateOf(false) }
  val heartScale by animateFloatAsState(
    targetValue = if (showHeartAnimation) 1.2f else 0f,
    label = "heart_scale"
  )

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = VyroCardSurface),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .border(0.5.dp, VyroBorder, RoundedCornerShape(16.dp))
      .testTag("post_card_${post.id}")
  ) {
    Column {
      // Header: Avatar, Username, Close Circle Tag, More menu
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        VyroAvatar(
          avatarUrl = post.userAvatar,
          size = 40.dp,
          isCloseCircle = post.isCloseCircle
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = post.userName,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = VyroTextPrimary
            )
            if (post.isCloseCircle) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(VyroGreenCloseCircle.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "Close Circle",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = VyroGreenCloseCircle
                )
              }
            }
          }
          if (post.location.isNotBlank()) {
            Text(
              text = post.location,
              fontSize = 11.sp,
              color = VyroTextTertiary
            )
          }
        }

        Box {
          IconButton(onClick = { showMenu = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = VyroTextSecondary)
          }
          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              text = { Text("Report Post") },
              onClick = {
                showMenu = false
                onReport()
              }
            )
            DropdownMenuItem(
              text = { Text("Copy Link") },
              onClick = { showMenu = false }
            )
          }
        }
      }

      // Media Image with double-tap like gesture
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1.2f)
          .background(VyroElevatedSurface)
          .pointerInput(Unit) {
            detectTapGestures(
              onDoubleTap = {
                onLike()
                showHeartAnimation = true
              }
            )
          },
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = post.mediaUri,
          contentDescription = "Post image",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Double tap heart popup
        if (showHeartAnimation) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = VyroHeartRed,
            modifier = Modifier
              .size(90.dp)
              .scale(heartScale)
          )
        }
      }

      // Action Bar: Like, Comment, Share, Save
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Like Button
        IconButton(
          onClick = onLike,
          modifier = Modifier.testTag("like_button_${post.id}")
        ) {
          Icon(
            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like",
            tint = if (post.isLiked) VyroHeartRed else VyroTextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
        Text(
          text = "${post.likesCount}",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = VyroTextPrimary
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Comment Button
        IconButton(
          onClick = onCommentClick,
          modifier = Modifier.testTag("comment_button_${post.id}")
        ) {
          Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = "Comment",
            tint = VyroTextPrimary,
            modifier = Modifier.size(22.dp)
          )
        }
        Text(
          text = "${post.commentsCount}",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = VyroTextPrimary
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Share Button
        IconButton(onClick = onShare) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = VyroTextPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save / Bookmark Button
        IconButton(
          onClick = onSave,
          modifier = Modifier.testTag("save_button_${post.id}")
        ) {
          Icon(
            imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Save",
            tint = if (post.isSaved) accent.primary else VyroTextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      // Caption & Tags
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 4.dp)
      ) {
        Text(
          text = post.caption,
          fontSize = 13.sp,
          color = VyroTextPrimary,
          lineHeight = 18.sp
        )

        if (post.tags.isNotEmpty()) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            post.tags.forEach { tag ->
              Text(
                text = tag,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = accent.primary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "View all ${post.commentsCount} comments",
          fontSize = 12.sp,
          color = VyroTextTertiary,
          modifier = Modifier.clickable { onCommentClick() }
        )
        Spacer(modifier = Modifier.height(10.dp))
      }
    }
  }
}
