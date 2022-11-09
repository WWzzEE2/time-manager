package com.example.myapplication

import android.content.Context
import android.util.Log
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
import com.example.myapplication.backstage.CourseInfo
import com.example.myapplication.backstage.CourseTemplate

// 缓存页面中修改时的course和template信息，当点击保存按钮时把修改的信息保存到后台
private var templateList = mutableStateListOf<CourseTemplate>()
private var course =
    CourseInfo("Name", 0, 0, emptyList<CourseTemplate>().toMutableList(), "Prompt", "Location")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPage(screenState: ScreenState) {
    Scaffold(topBar = { ChangeStat(screenState) }) {
        EditDetail()
    }
}

//顶部状态栏，包括back和done按钮
@Composable
fun ChangeStat(screenState: ScreenState) {
    val context = LocalContext.current
    SmallTopAppBar(
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
                saveData(context)
                screenState.goToCalendar()
            }) {
                Icon(Icons.Filled.Done, contentDescription = "Save")
            }
        },
    )
}

//编辑相关信息
@Composable
fun EditDetail() {
    Column(
        modifier = Modifier.padding(65.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        EditName()
        EditLocation()
        EditTimeChunk()

    }
}

//编辑时的文本框
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
    if (Label != "Column")
        valueList = mutableListOf<Long>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
    if (Label == "EndingTime")
        valueList.add(14)
    selectValue = when (Label) {
        "Column" -> templateList[Index].Column
        "StartingTime" -> templateList[Index].StartingTime
        else -> templateList[Index].EndingTime
    } + 1
    Box() {
        TextButton(onClick = { expanded = !expanded }) {
            if (Label == "Column")
                Text(text = "周$selectValue")
            else
                Text(text = "第${selectValue}节")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp),
            content = {
                valueList.forEach() {
                    DropdownMenuItem(text = {
                        if (Label == "Column")
                            Text(text = "周$it")
                        else
                            Text(text = "第${it}节")
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
        SimpleOutlinedTextField(Label = "Name", content = course.Name)
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
        SimpleOutlinedTextField(Label = "Location", content = course.Location)
    }
}

//边界时间信息
@Composable
fun EditTimeChunk() {
    addTemplateToList()
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
//    val expandTimeChunk = remember {
//        mutableStateListOf<Int>(1)
//    }
//    var LatestInt=2
//    Column(
//        modifier = Modifier
//    ) {
//        addTemplateToList()
//        LazyColumn(
//            modifier = Modifier.heightIn(0.dp,380.dp)
//        ) {
//            items(expandTimeChunk){i->
//             EditColumn(i)
//             EditStartingTime(i)
//             EditEndingTime(i)
//            Log.d("timechunkid", i.toString())
//            IconButton(
//                    onClick = {
//                        if (expandTimeChunk.size > 1) {
//                            removeTemplateFromList(expandTimeChunk.indexOf(i))
//                            expandTimeChunk.remove(i)
//                            LatestInt-=1
//                        }
//                    },
//            ) {
//                Icon(Icons.Filled.Delete, contentDescription ="DeleteTimeChunk")
//            }
//
//            Spacer(modifier = Modifier.height(10.dp))
//            Divider()
//            Spacer(modifier = Modifier.height(10.dp))
//            }
//        }
//        IconButton(
//            onClick = {
//                if (expandTimeChunk.size < 10) {
//                expandTimeChunk.add(LatestInt)
//                LatestInt+=1
//                addTemplateToList() } },
//        ) {
//            Icon(Icons.Outlined.Add, contentDescription ="AddTimeChunk")
//        }
//
//   }

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
        //SimpleOutlinedTextField(Label = "Column$Number", content = "")
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
        //SimpleOutlinedTextField(Label = "StartingTime$Number", content = "")
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
        //SimpleOutlinedTextField(Label = "EndingTime$Number", content = "")
        SelectTime(Label = "EndingTime", Number)
    }
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
        "Name" -> course.Name = content
        "Location" -> course.Location = content
        "StartingTime" -> {
            templateList[num].StartingTime = content.toLong() - 1
            if (templateList[num].EndingTime <= templateList[num].StartingTime)
                templateList[num].EndingTime = templateList[num].StartingTime + 1
        }
        "EndingTime" -> {
            templateList[num].EndingTime = content.toLong() - 1
            if (templateList[num].EndingTime <= templateList[num].StartingTime)
                templateList[num].EndingTime = templateList[num].StartingTime + 1
        }
        "Column" -> templateList[num].Column = content.toLong() - 1
    }
}

//保存信息
fun saveData(context: Context) {
    course.TimeInfo = templateList.toMutableList()
    val activity = context as MainActivity
    val schedule = activity.schedule
    Log.d("in addcourse", "111")
    Log.d("course", course.Name)
    Log.d("course", course.Location)
    Log.d("course", course.Prompt)
    if (schedule.addCourse(course))
        Log.d("lalal", "lalala")
    course.TimeInfo.forEachIndexed() { index, _ ->
        Log.d("index", index.toString())
        Log.d("Column", course.TimeInfo[index].Column.toString())
        Log.d("Start", course.TimeInfo[index].StartingTime.toString())
        Log.d("End", course.TimeInfo[index].EndingTime.toString())
    }
    templateList.clear()
    course =
        CourseInfo("Name", 0, 0, emptyList<CourseTemplate>().toMutableList(), "Prompt", "Location")
}

//向缓存列表中加入新的template
fun addTemplateToList() {
    val template = CourseTemplate(0, 0, 1, 1)
    template.info = course
    templateList.add(template)
}

//从缓存列表中删除template
fun removeTemplateFromList(Number: Int) {
    try {
        templateList.removeAt(Number)
    } finally {
    }
}


