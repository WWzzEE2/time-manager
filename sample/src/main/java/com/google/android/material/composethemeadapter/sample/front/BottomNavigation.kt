package com.example.myapplication.front

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import com.google.android.material.composethemeadapter.sample.front.ddl.DDLScreen
import com.example.myapplication.EditPage
import com.example.myapplication.SettingsPage
import com.example.myapplication.backstage.*
import com.example.myapplication.front.calendar.CalendarPage
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.backstage.CourseTemplate
import com.google.android.material.composethemeadapter.sample.backstage.WeekDay
import com.google.android.material.composethemeadapter.sample.backstage.getWeekDay
import java.util.*
import android.content.Context

class ScreenState (inPage: String) {
    var page by mutableStateOf(inPage)
    var editType = ""

    lateinit var myCourse: CourseTemplate
    fun goToCalendar() { page = "Calendar"}
    fun goToDeadline() { page = "Deadline"}
    fun goToEdit(myCourse_: CourseTemplate, editType_: String) {
        page = "Edit"
        myCourse = myCourse_
        editType = editType_
    }

    private lateinit var curWeekDay: WeekDay
    private lateinit var realWeekDay: WeekDay

    fun getCurWeek(): Long { return curWeekDay.week}
    fun getCurDay(): Long { return curWeekDay.day}
    fun getCurTime():Long { return curWeekDay.getTime()}
    fun setCurWeekDay(wd: WeekDay) {curWeekDay = wd}
    fun setCurWeek(w: Long) { curWeekDay.week = w}
    fun setCurDay(d: Long) { curWeekDay.day = d}

    fun getRealWeek(): Long { return realWeekDay.week}
    fun getRealDay(): Long { return realWeekDay.day}
    fun setRealWeekDay(wd: WeekDay) {realWeekDay = wd}
    fun setRealWeek(w: Long) { realWeekDay.week = w}
    fun setRealDay(d: Long) { realWeekDay.day = d}

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(context: Context) {

    val currentState by remember(){mutableStateOf(ScreenState("Calendar"))}
    val activity = LocalContext.current as MainActivity
    val schedule = activity.schedule
    val curWeekDay = getWeekDay(
        schedule.termInfo.startingTime,
        Calendar.getInstance().timeInMillis
    )
    currentState.setCurWeekDay(curWeekDay.copy())
    currentState.setRealWeekDay(curWeekDay.copy())

    val items = listOf("Calendar", "Deadline", "Setting")
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
            "Calendar" -> CalendarPage(
                currentState
            )
            "Deadline" -> DDLScreen(
                currentState
            )
            "Edit" ->  EditPage(
                currentState,
                currentState.myCourse,
                currentState.editType
            )
            "Setting" -> SettingsPage(context)
        }
    }
}




