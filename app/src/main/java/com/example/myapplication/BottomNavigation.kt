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

class ScreenState (private val inPage: String) {
    var page by mutableStateOf(inPage)
    fun goToCalendar() { page = "Calendar"}
    fun goToDeadline() { page = "Deadline"}
    fun goToEdit(){ page = "Edit"}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation() {

    var currentState by remember(){mutableStateOf(ScreenState("Calendar"))}

    val items = listOf("Calendar", "Deadline", "C")
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
                        selected = currentState.page == item,
                        onClick = {
                            currentState.page = item
                        }
                    )
                }
            }
        }
    ) {
        when(currentState.page) {
            "Calendar" -> CalendarPage(currentState)
            "Deadline" -> DDLScreen()
            "Edit" ->  EditPage(currentState)
        }
    }
}




