package com.nex.pricewiseandroidapp.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.nex.pricewiseandroidapp.auth.AuthViewModel
import com.nex.pricewiseandroidapp.data.model.UserProfile
import com.nex.pricewiseandroidapp.data.repository.UserRepository

val BackgroundGray = Color(0xFFF3F5F7)
val CardWhite = Color.White
val BrandBlue = Color(0xFF137FEC)
val TextDark = Color(0xFF0F172A)
val TextLight = Color(0xFF64748B)
val DangerRed = Color(0xFFEF4444)

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) { // Accept shared ViewModel
    val auth = FirebaseAuth.getInstance()
    val userRepository = UserRepository()

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) } // State for the dialog

    // --- Dialog --- 
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout() // Call logout on the shared ViewModel
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userRepository.getUserFromFirestore(
                currentUser.uid,
                onSuccess = { user ->
                    userProfile = user
                    isLoading = false
                },
                onFailure = { isLoading = false }
            )
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = { ProfileTopBar(onBackClick = { navController.popBackStack() }) }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandBlue)
            }
        } else if (userProfile != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                ProfileHeader(user = userProfile!!, onEditClick = { /* Handle Edit */ })
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        value = "15",
                        label = "Saved Deals",
                        icon = Icons.Outlined.FavoriteBorder,
                        color = BrandBlue
                    )
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        value = "$240",
                        label = "Total Savings",
                        icon = Icons.Outlined.CreditCard,
                        color = BrandBlue
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "My Activity")
                Spacer(modifier = Modifier.height(8.dp))
                MenuOptionItem(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "My Saved Deals",
                    subtitle = "View your favorite offers",
                    onClick = { /* Navigate */ })
                Spacer(modifier = Modifier.height(12.dp))
                MenuOptionItem(
                    icon = Icons.Outlined.History,
                    title = "Comparison History",
                    subtitle = "Past searches & items",
                    onClick = { /* Navigate */ })
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Account & Preferences")
                Spacer(modifier = Modifier.height(8.dp))
                MenuOptionItem(
                    icon = Icons.Outlined.CreditCard,
                    title = "Payment Methods",
                    subtitle = null,
                    onClick = { /* Navigate */ })
                Spacer(modifier = Modifier.height(12.dp))
                MenuOptionItem(
                    icon = Icons.Outlined.Settings,
                    title = "App Settings",
                    subtitle = null,
                    onClick = { /* Navigate */ })
                Spacer(modifier = Modifier.height(32.dp))

                // --- Logout Button ---
                Button(
                    onClick = { showLogoutDialog = true }, // Show the dialog
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = DangerRed
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color(0xFFFFE5E5), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("App Version 2.4.0", color = Color.LightGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Failed to load profile.")
            }
        }
    }
}

@Composable
fun ProfileTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextDark
            )
        }
        Text(text = "Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        IconButton(onClick = { /* Menu Action */ }) {
            Icon(
                Icons.Default.MoreHoriz,
                contentDescription = "More",
                tint = TextDark
            )
        }
    }
}

@Composable
fun ProfileHeader(user: UserProfile, onEditClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            if (user.profilePictureUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(user.profilePictureUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E7FF)), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = BrandBlue
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BrandBlue)
                    .clickable { onEditClick() }
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.username.ifEmpty { "User Name" },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Text(text = user.email, fontSize = 14.sp, color = BrandBlue)
    }
}

@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextLight
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, fontSize = 12.sp, color = TextLight)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MenuOptionItem(icon: ImageVector, title: String, subtitle: String?, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFEFF6FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextDark
                )
                if (subtitle != null) {
                    Text(text = subtitle, fontSize = 12.sp, color = TextLight)
                }
            }
        }
    }
}
