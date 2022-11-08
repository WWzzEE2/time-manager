package com.example.myapplication

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    Scaffold(
        topBar = { TopBar() },
    ) {
        Column(
            Modifier.width(500.dp)
        ) {
            Spacer(modifier = Modifier.height(300.dp))
            CenterText(
                text = "nothing here",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun TopBar() {
    SmallTopAppBar(
        title = {
            Text("Settings")
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Settings, contentDescription = "Localized description")
            }
        }
    )
}