package com.example.myapplication

import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import org.intellij.lang.annotations.JdkConstants.TitledBorderTitlePosition

val weekday = arrayListOf<String>("Mon","Tue","Wed","Thu","Fri","Sat","Sun")


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage()
{
    Scaffold(
        topBar = {TopBar()},
    ){

       CalendarGrid(0)
    }
}

@Composable
fun TopBar()
{
    SmallTopAppBar(
        title = { Text("TopAppBar") },
        actions = {
            // RowScope here, so these icons will be placed horizontally
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Localized description")
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Favorite, contentDescription = "Localized description")
            }
        })
}

@Composable
fun CalendarGrid(weekIndex:Int)
{
    LazyColumn(
        modifier = Modifier.height(760.dp)
    )
    {
        item{
            LazyRow(
                modifier = Modifier.padding(10.dp,0.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                for (i in 0..6) {
                    item { DailyList(weekIndex, i) }
                }
            }
        }
    }
}

@Composable
fun DailyList(weekIndex: Int,dayIndex: Int)
{
    Column(
        modifier = Modifier.padding(5.dp,0.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(text = weekday[dayIndex])
        Spacer(modifier = Modifier.padding(10.dp))
        for(i in 0..10)
        {
            var len:Int = (1..3).random()
            Button(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .width(120.dp)
                    .height(len * 60.dp + (len-1)*5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)

            ) {
                Text("class 1")
            }
        }

    }
}
