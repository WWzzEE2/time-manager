package com.google.android.material.composethemeadapter.sample.front.ddl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.JetLaggedHeaderTabs
import com.google.android.material.composethemeadapter.sample.backstage.getWeekDay
import com.example.myapplication.front.*
import com.example.myapplication.ui.theme.ddlBlockColor
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.R
import com.google.android.material.composethemeadapter.sample.backstage.CourseTemplate
import com.google.android.material.composethemeadapter.sample.backstage.DDlInfo
import com.google.android.material.datepicker.MaterialDatePicker

typealias DeadLine = DDlInfo

enum class DayTab(val title: Int) {
    Monday(R.string.Monday),
    Tuesday(R.string.Tuesday),
    Wednesday(R.string.Wednesday),
    Thursday(R.string.Thursday),
    Friday(R.string.Friday),
    Saturday(R.string.Saturday),
    Sunday(R.string.Sunday);

    fun getDay(Day: Int): DayTab =
        when (Day) {
            0 -> Monday
            1 -> Tuesday
            2 -> Wednesday
            3 -> Thursday
            4 -> Friday
            5 -> Saturday
            6 -> Sunday
            else -> Sunday
        }

}

enum class WeekTab {
    Week0, Week1, Week2, Week3, Week4, Week5, Week6, Week7, Week8, Week9, Week10, Week11, Week12, Week13, Week14, Week15, Week16, Week17;

    fun getWeek(Week: Int): WeekTab =
        when (Week) {
            0 -> Week0
            1 -> Week1
            2 -> Week2
            3 -> Week3
            4 -> Week4
            5 -> Week5
            6 -> Week6
            7 -> Week7
            8 -> Week8
            9 -> Week9
            10 -> Week10
            11 -> Week11
            12 -> Week12
            13 -> Week13
            14 -> Week14
            15 -> Week15
            16 -> Week16
            17 -> Week17
            else -> Week0
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadLineCard(
    ddl: DeadLine,
    onCloseTask: () -> Unit,
    modifier: Modifier = Modifier.padding(15.dp, 5.dp)
) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        if (isExpanded) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.background,
    )
    Button(
        onClick = { isExpanded = !isExpanded },
        shape = RoundedCornerShape(10.dp),
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = ddlBlockColor.getColor(0)),
        contentPadding = PaddingValues(10.dp),
        elevation = ButtonDefaults.buttonElevation(5.dp, 3.dp, 5.dp)
    )
    {
        Row(
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            Text(
                text = ddl.getString(MainActivity.GlobalInformation.activity.schedule.termInfo),
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = Modifier.width(10.dp))


            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = ddl.name,
                    style = MaterialTheme.typography.titleSmall,
                )
                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp,
                    // surfaceColor color will be changing gradually from primary to surface
                    color = surfaceColor,
                    // animateContentSize will change the Surface size gradually
                    modifier = Modifier
                        .animateContentSize()
                        .padding(1.dp)
                ) {
                    Text(
                        text = ddl.prompt,
                        modifier = Modifier.padding(all = 4.dp),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            IconButton(onClick = onCloseTask) {
                Icon(Icons.Filled.Check, contentDescription = "Close")
            }
        }
    }
}

@Composable
fun DeadLineList(
    list: List<DeadLine>,
    onCloseTask: (DeadLine) -> Unit,
) {

    LazyColumn {
        item { Spacer(modifier = Modifier.height(20.dp)) }
        items(
            items = list,
            key = { task -> listOf(task.endingTime , task.hashCode()) }
        )
        { message ->
            DeadLineCard(
                ddl = message,
                onCloseTask = { onCloseTask(message) }
            )
            Log.d("ddl list", message.id.toString())
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDLTopBar(
    screenState: ScreenState,
    DateAction: () -> Unit
) {
    TopAppBar(
        title = { Text("DDL时间表") },
        actions = {
            // RowScope here, so these icons will be placed horizontally
            IconButton(onClick = { DateAction() }) {
                Icon(Icons.Outlined.DateRange, contentDescription = "Localized description")
            }
            IconButton(onClick = {
                screenState.goToEdit(
                    CourseTemplate(0, 0, 1, 0),
                    "editDdl"
                )
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Localized description")
            }
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDLScreen(
    screenState: ScreenState,
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current as MainActivity
    val schedule = activity.schedule
    var list: MutableList<DDlInfo>
    var selectedWeekTab by remember {
        mutableStateOf(
            WeekTab.Week1.getWeek(
                screenState.getCurWeek().toInt()
            )
        )
    }
    var selectedDayTab by remember {
        mutableStateOf(
            DayTab.Monday.getDay(
                screenState.getCurDay().toInt()
            )
        )
    }
    Scaffold(
        topBar = {
            DDLTopBar(
                screenState,
                DateAction = {
                    val date = screenState.getCurTime()
                    val picker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(date)
                        .build()
                    activity.let {
                        picker.show(it.supportFragmentManager, picker.toString())
                        picker.addOnPositiveButtonClickListener {
                            picker.selection?.let { selectedDate ->
                                val Cur = getWeekDay(schedule.termInfo.startingTime, selectedDate)
                                screenState.setCurDay(Cur.day)
                                screenState.setCurWeek(Cur.week)
                                selectedWeekTab = WeekTab.Week1.getWeek(Cur.week.toInt())
                                selectedDayTab = DayTab.Monday.getDay(Cur.day.toInt())
                            }
                        }
                    }
                }
            )
        },
    ) { padding ->
        Column(modifier.padding(padding)) {


            JetLaggedHeaderTabs(
                screenState,
                onTabSelected = {
                    selectedWeekTab = it
                    screenState.setCurWeek(it.ordinal.toLong())
                },
                selectedTab = selectedWeekTab,
            )


            JetLaggedHeaderTabs(
                screenState,
                onTabSelected = {
                    selectedDayTab = it
                    screenState.setCurDay(it.ordinal.toLong())
                },
                selectedTab = selectedDayTab,
            )

            list = schedule.getDDlFromRelativeTime(
                selectedWeekTab.ordinal.toLong() - 1,
                selectedDayTab.ordinal.toLong()
            )
                .toMutableStateList()
            for (ddlinfo in list) {
                Log.d(
                    "ddl list",
                    ddlinfo.getString(MainActivity.GlobalInformation.activity.schedule.termInfo)
                        .toString()
                )
                Log.d("ddl list", ddlinfo.id.toString())
            }
            DeadLineList(
                list = list,
                onCloseTask = { task ->
                    list.remove(task)
                    schedule.removeDDl(task)
                    schedule.saveAll()
                    schedule.updateWidget()
                }
            )
            Spacer(Modifier.height(16.dp))
        }

    }
}

