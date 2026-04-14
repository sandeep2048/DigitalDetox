package com.sanson.digitaldetox.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sanson.digitaldetox.ui.navigation.Screen

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    BottomNavItem(Screen.Apps, "Apps", Icons.Filled.Apps, Icons.Outlined.Apps),
    BottomNavItem(Screen.Messages, "Messages", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline),
    BottomNavItem(Screen.Settings, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f),
        tonalElevation = 0.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.screen.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.screen) },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                    )
                )
            }
        }
    }
}
