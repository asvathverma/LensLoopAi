package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LensLoopBottomNav(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit
) {
    Surface(
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentRoute == "feed",
                onClick = { onNavigateToRoute("feed") }
            )
            
            // Search Tab
            BottomNavItem(
                icon = Icons.Default.Search,
                label = "Search",
                isSelected = currentRoute == "explore" || currentRoute == "search",
                onClick = { onNavigateToRoute("search") }
            )
            
            // Create Tab (Special Gradient Button)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onNavigateToRoute("create_post") },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 48.dp, height = 32.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFF77737))
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Reels Tab
            BottomNavItem(
                icon = Icons.Default.PlayArrow,
                label = "Reels",
                isSelected = currentRoute == "reels_feed",
                onClick = { onNavigateToRoute("reels_feed") }
            )
            
            // Profile Tab
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = currentRoute == "dashboard",
                onClick = { onNavigateToRoute("dashboard") }
            )
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) Color.White else Color(0xFF8E8E8E)
    
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
