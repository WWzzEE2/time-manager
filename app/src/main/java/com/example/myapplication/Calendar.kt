package com.example.myapplication

import android.graphics.Paint.Align
import android.widget.DatePicker
import android.widget.TextView
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.PopupProperties
import com.example.myapplication.backstage.CourseTemplate
import com.example.myapplication.backstage.DDlInfo
import com.example.myapplication.backstage.Schedule
import org.intellij.lang.annotations.JdkConstants.BoxLayoutAxis
import org.intellij.lang.annotations.JdkConstants.TitledBorderTitlePosition

val weekday = arrayListOf<String>("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(weekIndex: Int = 0) {
    var week = weekidx(remember { mutableStateOf(weekIndex) })
    Scaffold(
        topBar = { TopBar(week) },
    ) {
        Column() {
            CalendarGrid(week.index.value)
            println("regenerate")
        }
    }
}

private class weekidx(var index: MutableState<Int>)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar(week: weekidx) {

    SmallTopAppBar(
        title = {
            //WeekSelector()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Week ${week.index.value}",
                    modifier = Modifier.combinedClickable(
                        onClick = { println(" i clicked a button") },
                        onLongClick = { println(" i pressed a button") }
                    ))
                WeekSelector(week)
            }
        },
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
private fun WeekSelector(week: weekidx) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
        }
        val width = 120.dp
        //var offset by remember { mutableStateOf(0f) }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 140.dp)
                .width(width),
            offset = DpOffset(x = 20.dp, y = 0.dp)
        ) {
            for (i in 0..100) {
                DropdownMenuItem(
                    modifier = Modifier
                        .height(50.dp),
                    text = { CenterText(text = "week $i", modifier = Modifier.width(width)) },
                    onClick = {
                        week.index.value = i
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun CalendarGrid(weekIndex: Int) {
    println(weekIndex)
    LazyRow(
        modifier = Modifier.padding(5.dp, 0.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            Column() {
                val width = 100.dp
                Row() {
                    for (i in 0..6) {
                        CenterText(
                            modifier = Modifier
                                .padding(5.dp, 0.dp)
                                .width(width), text = weekday[i]
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.height(760.dp)
                ) {
                    item { Spacer(modifier = Modifier.padding(10.dp)) }
                    item {
                        Row()
                        {
                            for (i in 0..6) {
                                DailyList(Modifier.padding(5.dp, 5.dp), weekIndex, i, width = width)
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.padding(50.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TimeList() {
    Column(modifier = Modifier.width(20.dp)) {
        Text(text = "")
        Spacer(modifier = Modifier.padding(10.dp))
        for (i in 0..11) {

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DailyList(modifier: Modifier = Modifier, weekIndex: Int, dayIndex: Int, width: Dp = 100.dp) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {

        // get class info
        var i: Short = 0
        var activity = LocalContext.current as MainActivity
        var schedule = activity.schedule

        //render classBlock
        println(weekIndex)
        while (i <= 12) {
            var course: CourseTemplate? =
                schedule.getTemplate(i, dayIndex.toShort(), weekIndex.toShort())
            var ddl :DDlInfo
            var len: Int
            if (course == null) {
                len = 1
                ClassBlock(
                    MaterialTheme.colorScheme.background,
                    len,
                    {/*
                        TODO: your function here
                        Triggered when clicking an empty button. You should navigate to the "course add" page with necessary arguments
                     */
                    },
                    Modifier.width(width)
                ) { Text(text = "") }
            } else {
                var coursename = course.info.Name
                var courselocation = course.info.Location
                len = course?.EndingTime!! - course?.StartingTime!!

                ClassBlock(
                    MaterialTheme.colorScheme.secondary,
                    len,
                    MultiClick(
                        onClick = {},
                        doubleClick = {
                            /*
                                * TODO: your function here
                                * Triggered when doubleclicking a course button. turn to edit page with course info
                                * */
                        }
                    ),
                    Modifier.width(width),
//                        interactionSource
                ) {
                    LazyColumn() {
                        item {
                            CenterText(modifier = Modifier.width(width), text = coursename)
                            CenterText(modifier = Modifier.width(width), text = courselocation)
                        }
                    }
                }
            }

            i = (i + len).toShort()
        }
    }
}

@Composable
fun CenterText(modifier: Modifier = Modifier, text: String, fontsize: TextUnit = 13.sp) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = TextAlign.Center,
        fontSize = fontsize
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClassBlock(
    color: Color,
    len: Int,
    onclick: () -> Unit = {},
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    content: @Composable () -> Unit
) {
    Button(
        onClick = onclick,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .height(len * 60.dp + (len - 1) * 5.dp),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(10.dp),
        elevation = ButtonDefaults.buttonElevation(2.dp, 1.dp, 2.dp)

    ) {
        content()
    }
}

@Preview
@Composable
fun previewclassblock() {
    ClassBlock(
        color = Color.LightGray,
        len = 1,
        modifier = Modifier.width(100.dp)
    ) { Text(text = "114514") }
}

@Composable
inline fun MultiClick(
    time: Int = 300,
    crossinline onClick: () -> Unit,
    crossinline doubleClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableStateOf(value = 0L) }//使用remember函数记录上次点击的时间
    return {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime <= time) {//判断点击间隔,如果在间隔内则不回调
            println("double click")
            doubleClick()
        } else {
            println("click")
            onClick()
        }
        lastClickTime = currentTimeMillis
    }
}

