package com.example.myapplication

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import com.example.myapplication.backstage.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation() {
    //val navController = rememberNavController()
    var selectedPage by remember { mutableStateOf("A") }
    val items = listOf("A", "B", "C")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Notifications,
        Icons.Default.Settings
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        selected = selectedPage == item,
                        onClick = { selectedPage = item }
                    )
                }
            }
        }
    ) {
        when(selectedPage) {
            "A" -> CalendarPage()
            "B" -> DDLScreen()
        }
    }
}
