package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.local.entity.UserEntity
import com.example.ui.components.UserAvatar
import com.example.ui.theme.LachitBlue
import com.example.ui.theme.LachitCyan
import com.example.ui.theme.LachitRose

@Composable
fun AuthScreen(
    errorMessage: String?,
    demoUsers: List<UserEntity>,
    onLogin: (emailOrUser: String, pass: String) -> Unit,
    onRegister: (name: String, username: String, email: String, pass: String, dob: String, avatar: String) -> Unit,
    onSelectDemoUser: (UserEntity) -> Unit,
    onForgotPassword: (email: String) -> Unit = {},
    isLoading: Boolean = false,
    isFirebaseConnected: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Register

    // Login state
    var loginEmail by remember { mutableStateOf("") }
    var loginPass by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Register state
    var regName by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPass by remember { mutableStateOf("") }
    var regDob by remember { mutableStateOf("2000-01-01") }
    var regAvatar by remember { mutableStateOf("") }

    // Forgot password dialog
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Enter your registered email address and we'll send you a password reset link via Firebase Auth.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Email address") },
                        placeholder = { Text("you@example.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forgot_password_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotEmail.isNotBlank()) {
                            onForgotPassword(forgotEmail.trim())
                            showForgotDialog = false
                        }
                    },
                    enabled = forgotEmail.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = LachitBlue),
                    modifier = Modifier.testTag("send_reset_button")
                ) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Brand Logo & Header
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(LachitBlue, LachitCyan))),
            contentAlignment = Alignment.Center
        ) {
            Text("U", color = Color.White, fontWeight = FontWeight.Black, fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Unity",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = LachitBlue
        )

        Text(
            text = "Connect authentically with friends, creators & communities",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Backend Connectivity Badge
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFirebaseConnected) {
                    Color(0xFFE8F5E9)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isFirebaseConnected) Color(0xFF2E7D32) else LachitBlue)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFirebaseConnected) "Firebase Backend Live (Auth & Firestore)" else "Firebase Engine Initialized • Offline & Cloud Ready",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isFirebaseConnected) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message banner if any
        if (errorMessage != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Tabs
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Sign In", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_sign_in")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Create Account", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_create_account")
                    )
                }

                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (selectedTab == 0) {
                        // Login Fields
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("Email or Username") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input")
                        )

                        OutlinedTextField(
                            value = loginPass,
                            onValueChange = { loginPass = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    forgotEmail = loginEmail.trim()
                                    showForgotDialog = true
                                },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Text(
                                    "Forgot password?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LachitBlue
                                )
                            }
                        }

                        Button(
                            onClick = { onLogin(loginEmail, loginPass) },
                            enabled = !isLoading && loginEmail.isNotBlank() && loginPass.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LachitBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Signing In...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            } else {
                                Text("Sign In to Unity", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    } else {
                        // Register Fields
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("Alex Borgohain") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_fullname_input")
                        )

                        OutlinedTextField(
                            value = regUsername,
                            onValueChange = { regUsername = it },
                            label = { Text("Username") },
                            placeholder = { Text("alex_dev") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_username_input")
                        )

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("alex@example.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_email_input")
                        )

                        OutlinedTextField(
                            value = regPass,
                            onValueChange = { regPass = it },
                            label = { Text("Password (min 6 characters)") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_password_input")
                        )

                        OutlinedTextField(
                            value = regDob,
                            onValueChange = { regDob = it },
                            label = { Text("Date of Birth (YYYY-MM-DD)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_dob_input")
                        )

                        Button(
                            onClick = {
                                if (regName.isNotBlank() && regUsername.isNotBlank() && regEmail.isNotBlank() && regPass.isNotBlank()) {
                                    onRegister(regName, regUsername, regEmail, regPass, regDob, regAvatar)
                                }
                            },
                            enabled = !isLoading && regName.isNotBlank() && regUsername.isNotBlank() && regEmail.isNotBlank() && regPass.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LachitBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("register_submit_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Creating Account...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            } else {
                                Text("Register Account", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Demo 1-tap Account Switcher
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Quick Demo Accounts (1-Tap Switch):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    demoUsers.take(3).forEach { u ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onSelectDemoUser(u) }
                                .padding(4.dp)
                                .testTag("demo_user_${u.username}")
                        ) {
                            UserAvatar(avatarUrl = u.avatarUrl, name = u.fullName, size = 42.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = u.fullName.split(" ").firstOrNull() ?: "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (u.role == "ADMIN") "🛡️ Admin" else "User",
                                fontSize = 9.sp,
                                color = if (u.role == "ADMIN") LachitRose else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
