package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.sample.backstage.CourseInfo
import com.google.android.material.composethemeadapter.sample.backstage.CourseTemplate

import com.example.myapplication.front.*
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.backstage.save

// 缓存页面中修改时的course和template信息，当点击保存按钮时把修改的信息保存到后台
private var templateList = mutableStateListOf<CourseTemplate>()
private var course =
    CourseInfo("Name", 0, 0, emptyList<CourseTemplate>().toMutableList(), "Prompt", "Location")


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPage(
    screenState: ScreenState,
    myCourseTemplate: CourseTemplate,
    editType: String
) {
    Scaffold(topBar = { ChangeStat(screenState, myCourseTemplate, editType) })
    {padding ->
        EditDetail(Modifier.padding(padding))
    }
}

//顶部状态栏，包括back和done按钮
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeStat(screenState: ScreenState, myCourseTemplate: CourseTemplate, editType: String) {
    val context = LocalContext.current
    var timeConflict by rememberSaveable { mutableStateOf(false) }
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
                screenState.goToCalendar()
            }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = { Text("Edit") },
        actions = {
            IconButton(onClick = {
                timeConflict = !saveData(context, editType, myCourseTemplate)
                if (!timeConflict)
                    screenState.goToCalendar()
            }) {
                Icon(Icons.Filled.Done, contentDescription = "Save")
            }
        },
    )
    if (timeConflict) {
        AlertDialog(
            onDismissRequest = {
                timeConflict = false},
            title = { Text(text = "Warning!") },
            text = { Text(text = "Courses' time conflict!") },
            confirmButton = {
                TextButton(onClick = {
                    timeConflict = false
                }) {
                    Text(text = "Ok")
                }
            })
    }
}

//编辑相关信息
@Composable
fun EditDetail(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.padding(65.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        EditName()
        EditLocation()
        EditTimeChunk()
    }
}

//编辑时的文本框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleOutlinedTextField(Label: String, content: String) {
    var text by rememberSaveable { mutableStateOf(content) }
    if (Regex("[A-Za-z]+[0-9]").containsMatchIn(Label))
        Column() {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if (Regex("Column[0-9]").containsMatchIn(Label))
                        if (it.toInt() in 1..7) {
                            text = it
                            //changeData(Label, text)
                        } else
                            if (it.toInt() in 1..13) {
                                text = it
                                //changeData(Label,text)
                            }
                },
                label = { Text(Label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    else
        Column() {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    changeData(Label, text)
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
    if (Label != "Column")
        valueList = mutableListOf<Long>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
    selectValue = when (Label) {
        "Column" -> templateList[Index].column
        "StartingTime" -> templateList[Index].startingTime
        else -> templateList[Index].endingTime - 1
    } + 1
    Box() {
        TextButton(onClick = { expanded = !expanded }) {
            if (Label == "Column")
                Text(text = days[selectValue.toInt() - 1])
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

//编辑课程名
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
            Icons.Filled.Info,
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
fun changeData(type: String, content: String) {
    val operate: String
    var num = 0

    if (Regex("[A-Za-z]+[0-9]").containsMatchIn(type)) {
        num = type.last().code - 48
        operate = type.take(type.length - 1)
    } else
        operate = type

    when (operate) {
        "Name" -> course.name = content
        "Location" -> course.location = content
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
fun saveData(context: Context, editType: String, myCourseTemplate: CourseTemplate): Boolean {
    val activity = context as MainActivity
    val schedule = activity.schedule
    if (editType == "click_course")
        schedule.removeCourse(myCourseTemplate.info)
    templateList.forEach() {
        it.info = course
    }
    course.timeInfo = templateList.toMutableList()
    if (!schedule.addCourse(course)) {
        schedule.addCourse(myCourseTemplate.info)
        return false
    }
    templateList.clear()
    course =
        CourseInfo("Name", 0, 0, emptyList<CourseTemplate>().toMutableList(), "Prompt", "Location")
    schedule.saveAll()
    return true
}

//向缓存列表中加入新的template
fun addTemplateToList(template: CourseTemplate = CourseTemplate(0, 0, 1, 1)) {
    templateList.add(template)
}

//从缓存列表中删除template
fun removeTemplateFromList(Number: Int) {
    try {
        templateList.removeAt(Number)
    } finally {
    }
}


