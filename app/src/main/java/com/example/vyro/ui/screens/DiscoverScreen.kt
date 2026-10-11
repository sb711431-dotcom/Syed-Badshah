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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
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
import com.example.vyro.model.Post
import com.example.vyro.model.User
import com.example.vyro.ui.components.VyroAvatar

@Composable
fun DiscoverScreen(
  onSelectPost: (Post) -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }

  val categories = listOf("All", "Cyberpunk", "Modular Audio", "Nocturnal", "Travel", "Aesthetic", "Capsules")
  val friends by VyroRepository.friends.collectAsState()
  val posts by VyroRepository.posts.collectAsState()

  // Sample explore items
  val filteredPosts = posts.filter { post ->
    searchQuery.isBlank() ||
      post.caption.contains(searchQuery, ignoreCase = true) ||
      post.userName.contains(searchQuery, ignoreCase = true) ||
      post.tags.any { it.contains(searchQuery, ignoreCase = true) }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VyroBlack)
      .testTag("discover_screen"),
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
          placeholder = { Text("Search creators, tags, vibe...", color = VyroTextTertiary, fontSize = 14.sp) },
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
            .testTag("discover_search_field"),
          singleLine = true
        )
      }
    }

    // 2. Category Chips
    item {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(categories) { category ->
          val isSelected = selectedCategory == category
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = category },
            label = {
              Text(
                text = category,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = accent.primary,
              selectedLabelColor = Color.White,
              containerColor = VyroCardSurface,
              labelColor = VyroTextSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
              borderColor = if (isSelected) accent.primary else VyroBorder,
              enabled = true,
              selected = isSelected
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("filter_chip_$category")
          )
        }
      }
    }

    // 3. Trending Creators Spotlight
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = accent.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Trending Creators",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = VyroTextPrimary
            )
          }
          Text(
            text = "See all",
            fontSize = 12.sp,
            color = accent.primary,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(friends) { user ->
            CreatorCard(
              user = user,
              onToggleFriend = { VyroRepository.toggleFriend(user.id) }
            )
          }
        }
      }
    }

    // 4. Discovery Feed Grid Header
    item {
      Spacer(modifier = Modifier.height(20.dp))
      Text(
        text = "Explore Signals",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = VyroTextPrimary,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    // 5. Feed Grid
    items(filteredPosts.chunked(2)) { pair ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        pair.forEach { post ->
          Box(modifier = Modifier.weight(1f)) {
            DiscoveryPostTile(
              post = post,
              onClick = { onSelectPost(post) }
            )
          }
        }
        if (pair.size == 1) {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
fun CreatorCard(
  user: User,
  onToggleFriend: () -> Unit
) {
  val accent = LocalVyroAccent.current

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = VyroCardSurface),
    modifier = Modifier
      .width(140.dp)
      .border(0.5.dp, VyroBorder, RoundedCornerShape(16.dp))
      .testTag("creator_card_${user.username}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      VyroAvatar(
        avatarUrl = user.avatarUrl,
        size = 54.dp,
        isOnline = user.isOnline
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = user.displayName,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = VyroTextPrimary,
        maxLines = 1
      )
      Text(
        text = "@${user.username}",
        fontSize = 11.sp,
        color = VyroTextTertiary,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = onToggleFriend,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (user.isFriend) VyroElevatedSurface else accent.primary,
          contentColor = if (user.isFriend) VyroTextSecondary else Color.White
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(30.dp)
          .testTag("connect_btn_${user.id}")
      ) {
        Text(
          text = if (user.isFriend) "Connected" else "Connect",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun DiscoveryPostTile(
  post: Post,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = VyroCardSurface),
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(0.85f)
      .border(0.5.dp, VyroBorder, RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .testTag("discovery_post_${post.id}")
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      AsyncImage(
        model = post.mediaUri,
        contentDescription = post.caption,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      // Bottom gradient info overlay
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(80.dp)
          .align(Alignment.BottomCenter)
          .background(
            Brush.verticalGradient(
              listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
            )
          )
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          VyroAvatar(
            avatarUrl = post.userAvatar,
            size = 20.dp
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = post.userName.split(" ").first(),
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            maxLines = 1
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = VyroHeartRed,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${post.likesCount}",
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
