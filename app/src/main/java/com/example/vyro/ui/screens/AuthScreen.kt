package com.example.vyro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun AuthScreen(
  onAuthSuccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  val accent = LocalVyroAccent.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up

  var emailInput by remember { mutableStateOf("alex@vyro.network") }
  var passwordInput by remember { mutableStateOf("••••••••") }
  var usernameInput by remember { mutableStateOf("alex_vyro") }
  var displayNameInput by remember { mutableStateOf("Alex Rivera") }
  var isPasswordVisible by remember { mutableStateOf(false) }

  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showForgotPasswordDialog by remember { mutableStateOf(false) }
  var resetEmailInput by remember { mutableStateOf("") }
  var resetEmailSent by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VyroBlack)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("auth_screen")
  ) {
    // Background gradient glow circles
    Box(
      modifier = Modifier
        .size(300.dp)
        .align(Alignment.TopEnd)
        .background(
          Brush.radialGradient(
            listOf(accent.primary.copy(alpha = 0.15f), Color.Transparent)
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // VYRO Brand Monogram & Title
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.linearGradient(
              listOf(accent.primary, accent.secondary)
            )
          )
          .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "V",
          fontWeight = FontWeight.Black,
          fontSize = 36.sp,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "VYRO",
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = 4.sp,
        color = VyroTextPrimary
      )

      Text(
        text = "Next-Generation Ephemeral Social",
        fontSize = 13.sp,
        color = VyroTextSecondary,
        modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
      )

      // Auth Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VyroDarkSurface),
        modifier = Modifier
          .fillMaxWidth()
          .border(0.5.dp, VyroBorder, RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          // Tab Toggle: Sign In vs Sign Up
          TabRow(
            selectedTabIndex = selectedTab,
            containerColor = VyroElevatedSurface,
            contentColor = accent.primary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = accent.primary
              )
            }
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = {
                selectedTab = 0
                errorMessage = null
              },
              text = { Text("Sign In", fontWeight = FontWeight.Bold) }
            )
            Tab(
              selected = selectedTab == 1,
              onClick = {
                selectedTab = 1
                errorMessage = null
              },
              text = { Text("Sign Up", fontWeight = FontWeight.Bold) }
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Display Name & Username (for Sign Up)
          if (selectedTab == 1) {
            OutlinedTextField(
              value = displayNameInput,
              onValueChange = { displayNameInput = it },
              label = { Text("Display Name") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = VyroTextTertiary) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accent.primary,
                unfocusedBorderColor = VyroBorder,
                focusedTextColor = VyroTextPrimary,
                unfocusedTextColor = VyroTextPrimary
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_display_name_input"),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = usernameInput,
              onValueChange = { usernameInput = it },
              label = { Text("Username") },
              leadingIcon = { Text("@", color = VyroTextTertiary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accent.primary,
                unfocusedBorderColor = VyroBorder,
                focusedTextColor = VyroTextPrimary,
                unfocusedTextColor = VyroTextPrimary
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_username_input"),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Email Input
          OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = VyroTextTertiary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = accent.primary,
              unfocusedBorderColor = VyroBorder,
              focusedTextColor = VyroTextPrimary,
              unfocusedTextColor = VyroTextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_email_input"),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Password Input
          OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = VyroTextTertiary) },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Toggle password visibility",
                  tint = VyroTextTertiary
                )
              }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = accent.primary,
              unfocusedBorderColor = VyroBorder,
              focusedTextColor = VyroTextPrimary,
              unfocusedTextColor = VyroTextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_password_input"),
            singleLine = true
          )

          // Error banner
          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = errorMessage!!,
              color = VyroHeartRed,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Forgot password link (for Sign In)
          if (selectedTab == 0) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              TextButton(onClick = { showForgotPasswordDialog = true }) {
                Text("Forgot Password?", fontSize = 12.sp, color = accent.primary)
              }
            }
          } else {
            Spacer(modifier = Modifier.height(14.dp))
          }

          // Primary Submit Button
          Button(
            onClick = {
              if (emailInput.isBlank() || passwordInput.isBlank()) {
                errorMessage = "Please enter valid email and password"
                return@Button
              }
              if (selectedTab == 0) {
                VyroRepository.login(emailInput)
              } else {
                VyroRepository.signup(usernameInput, displayNameInput, emailInput)
              }
              onAuthSuccess()
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("auth_submit_button")
          ) {
            Text(
              text = if (selectedTab == 0) "Sign In" else "Create Account",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Instant Demo Test Button
          OutlinedButton(
            onClick = {
              VyroRepository.login("alex_vyro")
              onAuthSuccess()
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("demo_quick_login_button")
          ) {
            Text("⚡ Fast Demo Login (Alex Rivera)", fontSize = 13.sp, color = VyroTextSecondary)
          }
        }
      }
    }
  }

  // Forgot Password Dialog
  if (showForgotPasswordDialog) {
    AlertDialog(
      onDismissRequest = {
        showForgotPasswordDialog = false
        resetEmailSent = false
      },
      title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          if (!resetEmailSent) {
            Text(
              "Enter your account email to receive a secure recovery code.",
              fontSize = 13.sp,
              color = VyroTextSecondary
            )
            OutlinedTextField(
              value = resetEmailInput,
              onValueChange = { resetEmailInput = it },
              placeholder = { Text("your.email@example.com") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("reset_email_input")
            )
          } else {
            Text(
              "Password reset instructions have been sent to $resetEmailInput. Check your inbox.",
              fontSize = 13.sp,
              color = accent.primary
            )
          }
        }
      },
      confirmButton = {
        if (!resetEmailSent) {
          Button(
            onClick = {
              if (resetEmailInput.isNotBlank()) {
                resetEmailSent = true
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
          ) {
            Text("Send Instructions")
          }
        } else {
          Button(
            onClick = {
              showForgotPasswordDialog = false
              resetEmailSent = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
          ) {
            Text("Done")
          }
        }
      },
      dismissButton = {
        OutlinedButton(onClick = {
          showForgotPasswordDialog = false
          resetEmailSent = false
        }) {
          Text("Cancel")
        }
      },
      containerColor = VyroDarkSurface,
      textContentColor = VyroTextPrimary
    )
  }
}
