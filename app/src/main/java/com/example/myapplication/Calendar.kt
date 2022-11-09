package com.example.myapplication

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.myapplication.backstage.CourseTemplate
import com.example.myapplication.backstage.DDlInfo
import com.example.myapplication.backstage.getPastMin
import com.example.myapplication.ui.theme.*

val weekday = arrayListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

private class WeekIdx(
    var index: MutableState<Int>,
    var showDdl: MutableState<Boolean>,
    var showWeekSelector: MutableState<Boolean>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(screenState: ScreenState, weekIndex: Int = 0) {
    var week = WeekIdx(remember { mutableStateOf(weekIndex) },
        remember { mutableStateOf(true) },
        remember { mutableStateOf(false) })
    Scaffold(
        topBar = { TopBar(screenState, week) },
    ) {
        Column() {
            WeekSelector(week)
            Spacer(modifier = Modifier.height(5.dp))
            CalendarGrid(screenState, week.index.value, week.showDdl.value)
            println("regenerate")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar(screenState: ScreenState, week: WeekIdx) {
    SmallTopAppBar(
        title = {
            //WeekSelector()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Week ${week.index.value}",
                    modifier = Modifier.combinedClickable(
                        onClick = { println(" i clicked a button") },
                        onLongClick = { println(" i pressed a button") }
                    ))
                IconButton(onClick = {
                    week.showWeekSelector.value = !week.showWeekSelector.value
                }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                }
            }
        },
        actions = {
            // RowScope here, so these icons will be placed horizontally
            CalendarSetting(week)
            IconButton(
                onClick = {
                    screenState.goToEdit()
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Localized description")
            }
        }
    )
}

@Composable
private fun CalendarSetting(week: WeekIdx) {
    var expanded by remember { mutableStateOf(false) }
    Box()
    {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.Menu, contentDescription = "Localized description")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier,
            offset = DpOffset(x = 20.dp, y = 0.dp)
        ) {
            DropdownMenuItem(
                modifier = Modifier
                    .height(50.dp),
                text = { CenterText(text = "show ddl") },
                onClick = {
                    week.showDdl.value = !week.showDdl.value
                    expanded = false
                },
            )

        }
    }
}

@Composable
private fun WeekSelector(week: WeekIdx) {
    if (week.showWeekSelector.value) {
        val maxWeek = 100;
        ScrollableTabRow(
            selectedTabIndex = week.index.value,
            indicator = { tabPositions: List<TabPosition> ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(
                            tabPositions[week.index.value]
                        )
                        .height(2.dp),
                    color = Color.Gray
                )
            },
        ) {
            for (i in 0..maxWeek) {
                Tab(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(5.dp),
                    selected = i == week.index.value,
                    onClick = {
                        week.index.value = i;
//                        week.showWeekSelector.value = false;
                    },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.LightGray
                ) {
                    Text("week$i")
                }

            }
        }
    }
}


@Composable
fun CalendarGrid(screenState: ScreenState, weekIndex: Int, showDdllist: Boolean = true) {
    println(weekIndex)
    LazyRow(
        modifier = Modifier.padding(5.dp, 0.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            Column() {
                val width = 100.dp//width of each column
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
                                Box()
                                {
                                    DailyList(
                                        screenState,
                                        Modifier.padding(5.dp, 5.dp),
                                        weekIndex,
                                        i,
                                        width = width
                                    )

                                    if (showDdllist) {
                                        DdlLineList(
                                            Modifier.padding(10.dp, 0.dp),
                                            weekIndex,
                                            i,
                                            width = width - 10.dp
                                        )
                                    }
                                }
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
fun DailyList(
    screenState: ScreenState,
    modifier: Modifier = Modifier,
    weekIndex: Int,
    dayIndex: Int,
    width: Dp = 100.dp
) {

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
                schedule.getTemplate(i.toLong(), dayIndex.toLong(), weekIndex.toLong())
            var ddl: DDlInfo
            var len: Int
            if (course == null) {
                len = 1
                ClassBlock(
                    screenState,
                    MaterialTheme.colorScheme.background,
                    len,
                    {/*
                        TODO: your function here
                        Triggered when clicking an empty button. You should navigate to the "course add" page with necessary arguments
                     */
                        screenState.goToEdit()
                    },
                    Modifier.width(width)
                ) { Text(text = "") }
            } else {
                var coursename = course.info.name
                var courselocation = course.info.location
                len = (course.endingTime - course.startingTime).toInt()

                ClassBlock(
                    screenState,
                    courseBlockColor.getColor(i * (weekIndex + 1) + dayIndex),
                    len,
                    MultiClick(
                        onClick = {},
                        doubleClick = {
                            /*
                                * TODO: your function here
                                * Triggered when doubleclicking a course button. turn to edit page with course info
                                * */
                            screenState.goToEdit()
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
    screenState: ScreenState,
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

@Composable
fun DdlLineList(modifier: Modifier = Modifier, weekIndex: Int, dayIndex: Int, width: Dp = 100.dp) {

    Box(
        modifier = modifier,
    ) {
        var activity = LocalContext.current as MainActivity
        var schedule = activity.schedule
        var ddllist = schedule.getDDl(weekIndex.toLong(), dayIndex.toLong())
        var lastminute: Long = 0
        for (ddl in ddllist) {
            val pastminute = getPastMin(ddl.endingTime, schedule.termInfo)
            val parse: Int = (pastminute - lastminute).toInt()
            Column() {
                Spacer(modifier = modifier.height(parse * 1.dp))
                DdlLine(color = Red_T, modifier = Modifier.width(width))
            }
            lastminute = pastminute;
        }
        // get ddl info
    }
}

@Preview
@Composable
fun previewddllinelist() {
    DdlLineList(modifier = Modifier, weekIndex = 0, dayIndex = 0, width = 100.dp);
}

@Composable
fun DdlLine(
    color: Color,
    onclick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onclick,
        modifier = modifier.height(3.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {}
}

@Preview
@Composable
fun previewclassblock() {
    ClassBlock(
        ScreenState("Calendar"),
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

