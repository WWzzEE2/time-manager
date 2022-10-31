package com.example.myapplication

import android.graphics.Paint.Align
import android.widget.DatePicker
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.myapplication.backstage.CourseTemplate
import com.example.myapplication.backstage.Schedule
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
                Icon(Icons.Filled.Add, contentDescription = "Localized description")
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
                    item { DailyList(weekIndex, i.toInt()) }
                }
            }
        }
    }
}

@Composable
fun DailyList(weekIndex: Int,dayIndex: Int)
{
    val width = 100.dp
    Column(
        modifier = Modifier.padding(5.dp,0.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        CenterText(width = width, text = weekday[dayIndex])
        Spacer(modifier = Modifier.padding(10.dp))
        var i:Short = 0
        var activity = LocalContext.current as MainActivity
        var schedule = activity.schedule
        while(i<12)
        {
            var course:CourseTemplate?= schedule.getTemplate(dayIndex.toShort(),i,weekIndex.toShort())
            var len:Int = course?.EndingTime!! - course?.StartingTime!!
            ClassBlock ({ Text(text = "course 1")},len,width)
            i = (i + len).toShort()
        }

    }
}
@Composable
fun CenterText(width: Dp,text:String)
{
    Text(
        text = text,
        modifier = Modifier.width(width),
        textAlign = TextAlign.Center
    )
}
@Composable
fun ClassBlock(content:@Composable ()->Unit,len:Int,width:Dp)
{
    Button(
        onClick = { /*TODO*/ },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .width(width)
            .height(len * 60.dp + (len - 1) * 5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
        contentPadding = PaddingValues(10.dp)

    ) {
        content()
    }
}

