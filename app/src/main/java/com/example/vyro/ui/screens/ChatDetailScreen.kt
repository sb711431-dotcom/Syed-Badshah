package com.example.vyro.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.VyroHeartRed
import com.example.ui.theme.VyroTextPrimary
import com.example.ui.theme.VyroTextSecondary
import com.example.ui.theme.VyroTextTertiary
import com.example.vyro.data.VyroRepository
import com.example.vyro.model.Conversation
import com.example.vyro.model.Message
import com.example.vyro.ui.components.VyroAvatar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatDetailScreen(
  conversation: Conversation,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val accent = LocalVyroAccent.current
  var textInput by remember { mutableStateOf("") }
  var showMenu by remember { mutableStateOf(false) }
  var isDisappearingMode by remember { mutableStateOf(conversation.isDisappearingMode) }
  var isRecordingVoice by remember { mutableStateOf(false) }

  val messages = remember {
    mutableStateListOf<Message>().apply {
      addAll(VyroRepository.getMessagesForConversation(conversation.id))
    }
  }

  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
    VyroRepository.markConversationRead(conversation.id)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(VyroBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("chat_detail_screen")
  ) {
    // 1. Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(VyroDarkSurface)
        .border(0.5.dp, VyroBorder)
        .padding(horizontal = 8.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.testTag("chat_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = VyroTextPrimary
        )
      }

      VyroAvatar(
        avatarUrl = conversation.participant.avatarUrl,
        size = 40.dp,
        isOnline = conversation.participant.isOnline
      )

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = conversation.participant.displayName,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = VyroTextPrimary
        )
        Text(
          text = if (conversation.participant.isOnline) "Active now" else "@${conversation.participant.username}",
          fontSize = 11.sp,
          color = if (conversation.participant.isOnline) accent.primary else VyroTextTertiary
        )
      }

      // Disappearing mode toggle button
      IconButton(
        onClick = {
          isDisappearingMode = !isDisappearingMode
          VyroRepository.toggleDisappearingMode(conversation.id)
        },
        modifier = Modifier.testTag("toggle_disappearing_button")
      ) {
        Icon(
          imageVector = Icons.Default.Timer,
          contentDescription = "Disappearing Messages",
          tint = if (isDisappearingMode) accent.primary else VyroTextTertiary
        )
      }

      Box {
        IconButton(onClick = { showMenu = true }) {
          Icon(Icons.Default.MoreVert, contentDescription = "More", tint = VyroTextSecondary)
        }
        DropdownMenu(
          expanded = showMenu,
          onDismissRequest = { showMenu = false }
        ) {
          DropdownMenuItem(
            text = { Text("Block User") },
            onClick = {
              showMenu = false
              VyroRepository.blockUser(conversation.participant.id)
              onBack()
            }
          )
          DropdownMenuItem(
            text = { Text("Report User") },
            onClick = {
              showMenu = false
              VyroRepository.reportUser(conversation.participant.id, "Inappropriate chat behavior")
            }
          )
        }
      }
    }

    // 2. Disappearing Messages Alert Banner
    if (isDisappearingMode) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(accent.primary.copy(alpha = 0.12f))
          .border(0.5.dp, accent.primary.copy(alpha = 0.3f))
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "⚡ Disappearing Messages Active (24h). Media vanishes after viewing.",
          fontSize = 11.sp,
          color = accent.primary,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // 3. Message Thread
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(messages, key = { it.id }) { message ->
        val isMe = message.senderId == "user_me"
        MessageBubble(
          message = message,
          isMe = isMe,
          onReact = { emoji ->
            // react
          }
        )
      }
    }

    // 4. Input Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(VyroDarkSurface)
        .border(0.5.dp, VyroBorder)
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Camera / Photo shortcut
      IconButton(
        onClick = {
          // send quick photo
          val newMsg = Message(
            id = "msg_${System.currentTimeMillis()}",
            conversationId = conversation.id,
            senderId = "user_me",
            senderName = "Alex Rivera",
            mediaUri = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=900&q=80",
            text = "Sent a snapshot ⚡",
            isDisappearing = isDisappearingMode
          )
          messages.add(newMsg)
          VyroRepository.sendMessage(
            convId = conversation.id,
            text = "Sent a snapshot ⚡",
            mediaUri = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=900&q=80",
            isDisappearing = isDisappearingMode
          )
        },
        modifier = Modifier.testTag("chat_camera_button")
      ) {
        Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = accent.primary)
      }

      // Voice note button
      IconButton(
        onClick = {
          // Send sample voice note
          val newVoice = Message(
            id = "msg_v_${System.currentTimeMillis()}",
            conversationId = conversation.id,
            senderId = "user_me",
            senderName = "Alex Rivera",
            isVoice = true,
            voiceDurationSec = 14,
            isDisappearing = isDisappearingMode
          )
          messages.add(newVoice)
          VyroRepository.sendMessage(
            convId = conversation.id,
            text = "Voice message (0:14)",
            isVoice = true,
            isDisappearing = isDisappearingMode
          )
        },
        modifier = Modifier.testTag("chat_voice_button")
      ) {
        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = VyroTextSecondary)
      }

      // Text Input
      OutlinedTextField(
        value = textInput,
        onValueChange = { textInput = it },
        placeholder = { Text("Type a message...", color = VyroTextTertiary, fontSize = 14.sp) },
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
          .testTag("chat_message_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.width(6.dp))

      // Send Button
      IconButton(
        onClick = {
          if (textInput.isNotBlank()) {
            val newMsg = Message(
              id = "msg_${System.currentTimeMillis()}",
              conversationId = conversation.id,
              senderId = "user_me",
              senderName = "Alex Rivera",
              text = textInput.trim(),
              isDisappearing = isDisappearingMode
            )
            messages.add(newMsg)
            VyroRepository.sendMessage(
              convId = conversation.id,
              text = textInput.trim(),
              isDisappearing = isDisappearingMode
            )
            textInput = ""
          }
        },
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(accent.primary)
          .testTag("chat_send_button")
      ) {
        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
      }
    }
  }
}

@Composable
fun MessageBubble(
  message: Message,
  isMe: Boolean,
  onReact: (String) -> Unit
) {
  val accent = LocalVyroAccent.current
  val timeText = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
  ) {
    Box(
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isMe) 16.dp else 4.dp,
            bottomEnd = if (isMe) 4.dp else 16.dp
          )
        )
        .background(
          if (isMe) accent.primary else VyroElevatedSurface
        )
        .border(
          0.5.dp,
          if (isMe) accent.primary else VyroBorder,
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isMe) 16.dp else 4.dp,
            bottomEnd = if (isMe) 4.dp else 16.dp
          )
        )
        .padding(12.dp)
        .testTag("message_bubble_${message.id}")
    ) {
      Column {
        // Image attachment
        if (message.mediaUri.isNotBlank()) {
          AsyncImage(
            model = message.mediaUri,
            contentDescription = "Attachment",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(200.dp, 140.dp)
              .clip(RoundedCornerShape(10.dp))
          )
          Spacer(modifier = Modifier.height(6.dp))
        }

        // Voice Note waveform player simulation
        if (message.isVoice) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(180.dp)
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isMe) Color.White.copy(alpha = 0.3f) else accent.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              // Waveform representation
              Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                listOf(8, 16, 24, 12, 20, 28, 14, 22, 18, 10, 26, 14).forEach { h ->
                  Box(
                    modifier = Modifier
                      .width(3.dp)
                      .height(h.dp)
                      .clip(RoundedCornerShape(1.dp))
                      .background(if (isMe) Color.White else accent.secondary)
                  )
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "0:${if (message.voiceDurationSec < 10) "0${message.voiceDurationSec}" else message.voiceDurationSec}",
                fontSize = 10.sp,
                color = if (isMe) Color.White.copy(alpha = 0.8f) else VyroTextTertiary
              )
            }
          }
        }

        // Text
        if (message.text.isNotBlank()) {
          Text(
            text = message.text,
            fontSize = 14.sp,
            color = if (isMe) Color.White else VyroTextPrimary,
            lineHeight = 18.sp
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Timestamp and read receipt
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.End,
          modifier = Modifier.align(Alignment.End)
        ) {
          if (message.isDisappearing) {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = null,
              tint = if (isMe) Color.White.copy(alpha = 0.7f) else accent.primary,
              modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
          }
          Text(
            text = timeText,
            fontSize = 9.sp,
            color = if (isMe) Color.White.copy(alpha = 0.7f) else VyroTextTertiary
          )
          if (isMe) {
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Delivered",
              tint = Color.White.copy(alpha = 0.8f),
              modifier = Modifier.size(11.dp)
            )
          }
        }
      }
    }
  }
}
