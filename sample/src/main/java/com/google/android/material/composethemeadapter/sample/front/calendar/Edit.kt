package com.example.myapplication

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Task
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.example.myapplication.front.*
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.backstage.*
import com.google.android.material.datepicker.MaterialDatePicker


// 缓存页面中修改时的course和template信息，当点击保存按钮时把修改的信息保存到后台
private var templateList = mutableStateListOf<CourseTemplate>()
private var course =
    CourseInfo("", 0, 0, emptyList<CourseTemplate>().toMutableList(), "", "")
private var ddl = DDlInfo("", 0, 0, "", 0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPage(
    screenState: ScreenState,
    myCourseTemplate: CourseTemplate,
    editType: String
) {
    val context = LocalContext.current
    val activity = context as MainActivity
    val schedule = activity.schedule
    var timeConflict by rememberSaveable { mutableStateOf(false) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    var editType by rememberSaveable { mutableStateOf(editType) }
    var editCourse by rememberSaveable { mutableStateOf(true) }
    if (editType == "editDdl")
        editCourse = false

    Scaffold(topBar = {
        initData(myCourseTemplate, editType, context)
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = {
                    templateList.clear()
                    course = CourseInfo(
                        "Name",
                        0,
                        0,
                        emptyList<CourseTemplate>().toMutableList(),
                        "Prompt",
                        "Location"
                    )
                    if (editCourse){screenState.goToCalendar()}
                    else {screenState.goToDeadline()}
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            title = { Text("Edit") },
            actions = {
                Row {
                    IconButton(onClick = {
                        editCourse = !editCourse
                    }) {
                        if (editCourse){Icon(Icons.Rounded.Task, contentDescription = "Switch edit type")}
                        else {Icon(Icons.Rounded.CalendarToday, contentDescription = "Switch edit type")}
                    }
                    if (editType == "click_course") {
                        IconButton(onClick = {
                            confirmDelete = !confirmDelete
                        }) {
                            Icon(
                                Icons.Outlined.DeleteForever,
                                contentDescription = "Switch edit type"
                            )
                        }
                    }
                    IconButton(onClick = {
                        if (!editCourse)
                            editType = "editDdl"
                        timeConflict = !saveData(context, myCourseTemplate, editType)
                        if (!timeConflict) {
                            schedule.saveAll()
                            screenState.goToCalendar()
                        }
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = "Save")
                    }
                }
            },
        )
        if (confirmDelete) {
            val warningContent = "This action will delete your course!"
            AlertDialog(
                onDismissRequest = {
                    confirmDelete = false
                },
                title = { Text(text = "Warning!") },
                text = { Text(warningContent) },
                dismissButton = {
                    TextButton(onClick = {
                        confirmDelete = false
                    }) {
                        Text(text = "Cancel")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        schedule.removeCourse(course)
                        schedule.saveAll()
                        screenState.goToCalendar()
                    }) {
                        Text(text = "Confirm")
                    }
                })
        }
        if (timeConflict) {
            var warningContent = "Courses' time conflict!"
            if (editType == "editDdl")
                warningContent = "Please select valid time!"
            AlertDialog(
                onDismissRequest = {
                    timeConflict = false
                },
                title = { Text(text = "Warning!") },
                text = { Text(warningContent) },
                confirmButton = {
                    TextButton(onClick = {
                        timeConflict = false
                    }) {
                        Text(text = "Ok")
                    }
                })
        }
    })
    { padding ->
        if (editCourse)
            EditCourse(Modifier.padding(padding))
        else
            EditDdl(Modifier.padding(padding), screenState)
    }

}

//编辑相关信息
@Composable
fun EditCourse(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(65.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        EditName()
        EditLocation()
        EditPrompt()
        EditTimeChunk()
    }
}

//编辑DDL相关信息
@Composable
fun EditDdl(modifier: Modifier = Modifier, screenState: ScreenState) {
    Column(
        modifier = modifier.padding(65.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                modifier = Modifier
                    .width(35.dp)
                    .height(35.dp),
                contentDescription = "Name"
            )
            SimpleOutlinedTextField(Label = "Name", content = ddl.name, "editDdl")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                modifier = Modifier
                    .width(35.dp)
                    .height(35.dp),
                contentDescription = "Prompt"
            )
            SimpleOutlinedTextField(Label = "Prompt", content = ddl.name, "editDdl")
        }
        EditDdlTime(screenState = screenState)
    }
}

//编辑时的文本框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleOutlinedTextField(Label: String, content: String, editType: String = "editCourse") {
    var text by rememberSaveable { mutableStateOf(content) }
    Column() {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                changeData(Label, text, editType)
            },
            label = { Text(Label) },
        )
    }
}

//编辑时间信息时选择时间
@Composable
fun SelectTime(Label: String, Index: Int) {
    var valueList = mutableListOf<Long>(1, 2, 3, 4, 5, 6, 7)
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectValue by rememberSaveable { mutableStateOf<Long>(1) }
    val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val period = arrayOf("Every Week", "Odd Weeks", "Dual Weeks")
    if (Label != "Column")
        valueList = mutableListOf<Long>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
    if (Label == "Period")
        valueList = mutableListOf(0, 2, 1)
    selectValue = when (Label) {
        "Column" -> templateList[Index].column
        "StartingTime" -> templateList[Index].startingTime
        "Period" -> templateList[Index].period - 1
        else -> templateList[Index].endingTime - 1
    } + 1
    Box() {
        TextButton(onClick = { expanded = !expanded }) {
            if (Label == "Column")
                Text(text = days[selectValue.toInt() - 1])
            else if (Label == "Period")
                Text(text = period[selectValue.toInt()])
            else
                Text(text = "Course${selectValue}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp),
            content = {
                valueList.forEach() {
                    DropdownMenuItem(text = {
                        if (Label == "Column")
                            Text(text = days[it.toInt() - 1])
                        else if (Label == "Period")
                            Text(text = period[it.toInt()])
                        else
                            Text(text = "Course${it}")
                    }, onClick = {
                        expanded = !expanded
                        selectValue = it

                        changeData(Label + "$Index", it.toString())
                    })
                }
            })
    }
}

//编辑课程/DDL名
@Composable
fun EditName() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Name"
        )
        SimpleOutlinedTextField(Label = "Name", content = course.name)
    }
}

//编辑上课地点
@Composable
fun EditLocation() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.LocationOn,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Location"
        )
        SimpleOutlinedTextField(Label = "Location", content = course.location)

    }
}

//编辑课程描述
@Composable
fun EditPrompt() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.Info,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Prompt"
        )
        SimpleOutlinedTextField(Label = "Prompt", content = course.prompt)

    }
}


//边界时间信息
@Composable
fun EditTimeChunk() {
    LazyColumn(
        modifier = Modifier.heightIn(0.dp, 380.dp)
    ) {
        templateList.forEachIndexed() { index, _ ->
            item {
                EditColumn(index)
                EditStartingTime(index)
                EditEndingTime(index)
                EditPeriod(index)
            }
            item {
                IconButton(
                    onClick = {
                        if (templateList.size > 1) {
                            removeTemplateFromList(index)
                        }
                    },
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "DeleteTimeChunk")
                }
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        item {
            IconButton(
                onClick = {
                    addTemplateToList()
                },
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "AddTimeChunk")
            }
        }
    }
}

//编辑上课是一周中的哪一天
@Composable
fun EditColumn(Number: Int = 0) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.DateRange,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Column"
        )
        SelectTime(Label = "Column", Number)
    }
}

//编辑课程开始时间
@Composable
fun EditStartingTime(Number: Int = 0) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.Timer,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "StartingTime"
        )
        SelectTime(Label = "StartingTime", Number)
    }
}

//编辑课程结束时间
@Composable
fun EditEndingTime(Number: Int = 0) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
        )
        SelectTime(Label = "EndingTime", Number)
    }
}


//编辑上课间隔
@Composable
fun EditPeriod(Number: Int = 0) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
        )
        SelectTime(Label = "Period", Number)
    }
}

@Composable
fun EditDdlTime(screenState: ScreenState) {
    //test
    val activity = LocalContext.current as MainActivity
    var mTime by remember { mutableStateOf("Select Time") }
    var mDate by remember { mutableStateOf("Select Date") }
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    )
    {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                Icons.Outlined.DateRange, contentDescription = "Edit deadline date",
                Modifier
                    .width(35.dp)
                    .height(35.dp),
            )
            Text(text = mDate, Modifier.clickable() {
                val date = screenState.getCurTime()
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(date)
                    .build()
                activity.let {
                    datePicker.show(it.supportFragmentManager, datePicker.toString())
                    datePicker.addOnPositiveButtonClickListener {
                        datePicker.selection?.let { selectedDate ->
                            ddl.endingTime = selectedDate
                            mTime = "Select Time"
                            mDate = transToString(selectedDate)
                        }
                    }
                }
            })
        }
        val mContext = LocalContext.current
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                Icons.Default.Notifications, contentDescription = "Edit Deadline time",
                Modifier
                    .width(35.dp)
                    .height(35.dp),
            )
            Text(text = mTime, Modifier.clickable() {
                val mHour = 0
                val mMinute = 0
                val mTimePickerDialog = TimePickerDialog(
                    mContext,
                    { _, mHour: Int, mMinute: Int ->
                        mTime = String.format("%02d: %02d", mHour, mMinute)
                        ddl.endingTime += (mHour * 3600 * 1000L + mMinute * 60 * 1000L + 60 * 1000L)
                    }, mHour, mMinute, false
                )
                mTimePickerDialog.show()
            })
        }
    }
}

fun initData(myCourseTemplate: CourseTemplate, editType: String, context: Context) {
    val activity = context as MainActivity
    val schedule = activity.schedule
    if (editType != "click_course") {
        course.startingTime = schedule.termInfo.startingTime
        course.endingTime = schedule.termInfo.startingTime + 1000L * 3600 * 24 * 7 * 20
    }
    if (editType == "click_null")
        addTemplateToList(myCourseTemplate)
    else if (editType == "click_course") {
        course = myCourseTemplate.info.copy()
        templateList = course.timeInfo.map { it.copy() }.toMutableStateList()
    } else
        if (templateList.isEmpty())
            addTemplateToList()
}


//将编辑的信息同步到缓存中
fun changeData(type: String, content: String, editType: String = "editCourse") {
    val operate: String
    var num = 0

    if (Regex("[A-Za-z]+[0-9]").containsMatchIn(type)) {
        num = type.last().code - 48
        operate = type.take(type.length - 1)
    } else
        operate = type

    when (operate) {
        "Name" -> {
            if (editType == "editCourse")
                course.name = content
            else
                ddl.name = content
        }
        "Location" -> course.location = content
        "Period" -> templateList[num].period = content.toLong()
        "Prompt" ->
            if (editType == "editCourse")
                course.prompt = content
            else
                ddl.prompt = content
        "StartingTime" -> {
            templateList[num].startingTime = content.toLong() - 1
            if (templateList[num].endingTime <= templateList[num].startingTime)
                templateList[num].endingTime = templateList[num].startingTime + 1
        }
        "EndingTime" -> {
            templateList[num].endingTime = content.toLong()
            if (templateList[num].endingTime <= templateList[num].startingTime)
                templateList[num].endingTime = templateList[num].startingTime + 1
        }
        "Column" -> templateList[num].column = content.toLong() - 1
    }
}

//保存信息
fun saveData(
    context: Context,
    myCourseTemplate: CourseTemplate,
    editType: String
): Boolean {
    val activity = context as MainActivity
    val schedule = activity.schedule
    if (editType == "editDdl") {
        if (ddl.endingTime == 0L)
            return false
        ddl.id = System.currentTimeMillis()
        schedule.addDDl(ddl)
        ddl = DDlInfo("", 0, 0, "", 0)
        return true
    }
    if (editType == "click_course")
        schedule.removeCourse(myCourseTemplate.info)
        templateList.forEach() {
        it.info = course
    }
    course.timeInfo = templateList.toMutableList()
    if (!schedule.addCourse(course)) {
        if (editType == "click_course")
            schedule.addCourse(myCourseTemplate.info)
        return false
    }
    templateList.clear()
    course =
        CourseInfo(
            "",
            0,
            0,
            emptyList<CourseTemplate>().toMutableList(),
            "",
            ""
        )
    return true
}

//向缓存列表中加入新的template
fun addTemplateToList(template: CourseTemplate = CourseTemplate(0, 0, 1, 0)) {
    templateList.add(template)
}

//从缓存列表中删除template
fun removeTemplateFromList(Number: Int) {
    try {
        templateList.removeAt(Number)
    } finally {
    }
}


