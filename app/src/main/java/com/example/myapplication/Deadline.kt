package com.example.myapplication

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.backstage.DDlInfo
import com.example.myapplication.backstage.WeekDay
import com.example.myapplication.backstage.getWeekDay
import com.example.myapplication.backstage.termInfo


typealias DeadLine= DDlInfo

enum class WeekTab(val title: Int) {
    Monday(R.string.Monday),
    Tuesday(R.string.Tuesday),
    Wednesday(R.string.Wednesday),
    Thursday(R.string.Thursday),
    Friday(R.string.Friday),
    Saturday(R.string.Saturday),
    Sunday(R.string.Sunday);
    fun GetWeek(Day:Int):WeekTab=
        when (Day){
            0->  Monday
            1->  Tuesday
            2->  Wednesday
            3->  Thursday
            4->  Friday
            5->  Saturday
            6->  Sunday
            else ->Sunday
        }

}

enum class MoonTab {
    Week1,Week2,Week3,Week4,Week5,Week6,Week7,Week8,Week9,Week10,Week11,Week12,Week13,Week14,Week15,Week16,Week17;
    fun GetMoon(Week:Int):MoonTab=
        when (Week){
            1-> Week1
            2-> Week2
            3-> Week3
            4-> Week4
            5-> Week5
            6-> Week6
            7-> Week7
            8-> Week8
            9-> Week9
            10-> Week10
            11-> Week11
            12-> Week12
            13-> Week13
            14-> Week14
            15-> Week15
            16-> Week16
            17-> Week17
            else -> Week1
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadLineCard(
    ddl:DeadLine,
    onCloseTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val surfaceColor by animateColorAsState(
        if (isExpanded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background,
    )

    Row(modifier = Modifier
        .padding(all = 8.dp)
    ) {
        Column (modifier = Modifier
            .clickable { isExpanded = !isExpanded }
            .weight(1f)
        ){
            Text(text = ddl.getString(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            val Cur: WeekDay= getWeekDay(termInfo.StartingTime,ddl.EndingTime)
            Text(text = "Week"+Cur.week+" Day "+Cur.day,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))


        Column (modifier = Modifier
            .clickable { isExpanded = !isExpanded }
            .weight(1f)
        ){
            Text(text = ddl.Name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            // Add a vertical space between the author and message texts
            Spacer(modifier = Modifier.height(4.dp))
            Surface(shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                // surfaceColor color will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text =  ddl.Prompt,
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

@Composable
fun DeadLineList(
    list: List<DeadLine>,
    onCloseTask: (DeadLine) -> Unit,
) {
    LazyColumn {
        items(
            items =list,
            key={task -> task.EndingTime+1.0/task.Id}
        )
        { message ->
            DeadLineCard(
                ddl=message,
                onCloseTask ={onCloseTask(message)}
            )
        }
    }
}



@Composable
fun DDLTopBar()
{
    SmallTopAppBar(
        title = { Text("DDL时间表") },
        actions = {
            // RowScope here, so these icons will be placed horizontally
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Localized description")
            }
        })
}

val CurrentTime=System.currentTimeMillis()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDLScreen(modifier: Modifier = Modifier) {
    var activity = LocalContext.current as MainActivity
    var schedule = activity.schedule
    val Cur: WeekDay= getWeekDay(termInfo.StartingTime,CurrentTime)
    var list = remember { schedule.getDDl(Cur.week.toInt(),Cur.day.toInt()).toMutableStateList() }
    Scaffold(
        topBar = {DDLTopBar()},
    ){
        Column(modifier) {

            var selectedMoonTab by remember { mutableStateOf(MoonTab.Week1.GetMoon(Cur.week.toInt())) }
            JetLaggedHeaderTabs(
                onTabSelected = { selectedMoonTab = it },
                selectedTab = selectedMoonTab,
            )
            
            var selectedWeekTab by remember { mutableStateOf(WeekTab.Monday.GetWeek(Cur.day.toInt())) }
            JetLaggedHeaderTabs(
                onTabSelected = { selectedWeekTab = it },
                selectedTab = selectedWeekTab,
            )

            list=schedule.getDDl(selectedMoonTab.ordinal,selectedWeekTab.ordinal).toMutableStateList()
            Text(text = "Today is Week"+Cur.week+" Day "+Cur.day)
            DeadLineList(
                list=list,
                onCloseTask={ task -> list.remove(task) }
            )
            Spacer(Modifier.height(16.dp))
        }

    }
}

