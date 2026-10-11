package com.example.vyro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalVyroAccent
import com.example.ui.theme.VyroBlack
import com.example.ui.theme.VyroBorder
import com.example.ui.theme.VyroCardSurface
import com.example.ui.theme.VyroDarkSurface
import com.example.ui.theme.VyroElevatedSurface
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.ui.theme.VyroTextTertiary
import com.example.vyro.data.VyroRepository
import com.example.vyro.model.Conversation
import com.example.vyro.model.User
import com.example.vyro.ui.components.VyroAvatar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessagesScreen(
  onOpenConversation: (Conversation) -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current
  var searchQuery by remember { mutableStateOf("") }

  val conversations by VyroRepository.conversations.collectAsState()
  val friends by VyroRepository.friends.collectAsState()

  val filteredConversations = conversations.filter {
    searchQuery.isBlank() ||
      it.participant.displayName.contains(searchQuery, ignoreCase = true) ||
      it.participant.username.contains(searchQuery, ignoreCase = true) ||
      (it.lastMessage?.text?.contains(searchQuery, ignoreCase = true) == true)
  }

  val onlineFriends = friends.filter { it.isOnline }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VyroBlack)
      .testTag("messages_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Search Bar
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search direct messages...", color = VyroTextTertiary, fontSize = 14.sp) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = VyroTextTertiary)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = VyroTextTertiary)
              }
            }
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = VyroDarkSurface,
            unfocusedContainerColor = VyroDarkSurface,
            focusedBorderColor = accent.primary,
            unfocusedBorderColor = VyroBorder,
            focusedTextColor = VyroTextPrimary,
            unfocusedTextColor = VyroTextPrimary
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_messages_input"),
          singleLine = true
        )
      }
    }

    // 2. Active Now Row
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, bottom = 12.dp)
      ) {
        Text(
          text = "Active Signals",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = VyroTextSecondary,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          items(onlineFriends) { friend ->
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable {
                  val conv = conversations.find { it.participant.id == friend.id }
                    ?: Conversation(
                      id = "conv_${friend.id}",
                      participant = friend,
                      lastMessage = null
                    )
                  onOpenConversation(conv)
                }
                .testTag("online_friend_${friend.id}")
            ) {
              VyroAvatar(
                avatarUrl = friend.avatarUrl,
                size = 54.dp,
                isOnline = true
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = friend.displayName.split(" ").first(),
                fontSize = 11.sp,
                color = VyroTextPrimary,
                maxLines = 1
              )
            }
          }
        }
      }
    }

    // 3. Conversation Thread List
    item {
      Text(
        text = "Messages",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = VyroTextPrimary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
      )
    }

    if (filteredConversations.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No conversations found",
            color = VyroTextTertiary,
            fontSize = 14.sp
          )
        }
      }
    } else {
      items(filteredConversations, key = { it.id }) { conv ->
        ConversationListItem(
          conversation = conv,
          onClick = { onOpenConversation(conv) }
        )
      }
    }
  }
}

@Composable
fun ConversationListItem(
  conversation: Conversation,
  onClick: () -> Unit
) {
  val accent = LocalVyroAccent.current
  val lastMsg = conversation.lastMessage
  val timeText = if (lastMsg != null) {
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(lastMsg.timestamp))
  } else ""

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = VyroCardSurface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 4.dp)
      .border(0.5.dp, VyroBorder, RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("conversation_item_${conversation.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      VyroAvatar(
        avatarUrl = conversation.participant.avatarUrl,
        size = 50.dp,
        isOnline = conversation.participant.isOnline,
        isCloseCircle = conversation.participant.isCloseCircle
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = conversation.participant.displayName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = VyroTextPrimary
          )
          Text(
            text = timeText,
            fontSize = 11.sp,
            color = VyroTextTertiary
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (conversation.isDisappearingMode) {
              Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Disappearing",
                tint = accent.primary,
                modifier = Modifier
                  .size(13.dp)
                  .padding(end = 3.dp)
              )
            }
            if (lastMsg?.isVoice == true) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = accent.secondary,
                modifier = Modifier
                  .size(14.dp)
                  .padding(end = 3.dp)
              )
            }
            Text(
              text = lastMsg?.text ?: "Start chatting...",
              fontSize = 12.sp,
              color = if (conversation.unreadCount > 0) VyroTextPrimary else VyroTextSecondary,
              fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
              maxLines = 1
            )
          }

          if (conversation.unreadCount > 0) {
            Box(
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(accent.primary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${conversation.unreadCount}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}
