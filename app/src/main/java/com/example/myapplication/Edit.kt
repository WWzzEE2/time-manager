package com.example.myapplication

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import com.example.myapplication.backstage.CourseInfo
import com.example.myapplication.backstage.CourseTemplate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


private var templateList = mutableListOf<CourseTemplate>()
private var course = CourseInfo("Name", 0, 0, templateList, "Prompt", "Location")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPage(screenState:ScreenState){
    Scaffold(topBar = {ChangeStat(screenState)}) {
        EditDetail()
    }
}

@Composable
fun ChangeStat(screenState:ScreenState){
    val context = LocalContext.current
    SmallTopAppBar(
        navigationIcon = {
            IconButton(onClick = { screenState.goToCalendar() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = { Text("Edit")},
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

@Composable
fun EditDetail(){
    Column(
        modifier = Modifier.padding(65.dp, 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        EditName()
        EditLocation()
        EditTimeChunk()

    }
}

@Composable
fun SimpleOutlinedTextField(Label:String, content:String) {
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
                    }
                else
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
                        changeData(Label, text) },
                label = { Text(Label) },
            )
        }
}

@Composable
fun SelectTime(Label:String, Index: Int){
    var valueList  = mutableListOf(1,2,3,4,5,6,7)
    var expanded by rememberSaveable{ mutableStateOf(false) }
    var selectValue by rememberSaveable{ mutableStateOf<Long>(1) }
    if (Label != "Column")
        valueList = mutableListOf(1,2,3,4,5,6,7,8,9,10,11,12,13)
    selectValue = when(Label) {
        "Column" -> templateList[Index].Column
        "StartingTime" -> templateList[Index].StartingTime
        else -> templateList[Index].EndingTime
    }
    Box() {
        Text(text = selectValue.toString(),
            modifier = Modifier.clickable { expanded = !expanded }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            content = {
            valueList.forEach(){
                DropdownMenuItem( text = { Text(text = it.toString())},  onClick = {
                    expanded = !expanded
                    selectValue = it.toLong()
                    changeData(Label, it.toString())
                })
                }
            })
        }
    }


@Composable
fun EditName(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Name")
        SimpleOutlinedTextField(Label = "Name", content = "software")
        }
}



@Composable
fun EditTimeChunk(){
    addTemplateToList()
    var length by rememberSaveable{
        mutableStateOf(templateList.size)
    }
    LazyColumn(
            modifier = Modifier.heightIn(0.dp,380.dp)
       ) {
        for (i in 1..length) {
             EditColumn(i)
             EditStartingTime(i)
             EditEndingTime(i)
            Log.d("timechunkid", i.toString())
            IconButton(
                    onClick = {
                        if (expandTimeChunk.size > 1) {
                            removeTemplateFromList(expandTimeChunk.indexOf(i))
                            expandTimeChunk.remove(i)
                            LatestInt-=1
                        }
                    },
            ) {
                Icon(Icons.Filled.Delete, contentDescription ="DeleteTimeChunk")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(10.dp))
            }
        }
        IconButton(
            onClick = {
                if (expandTimeChunk.size < 10) {
                expandTimeChunk.add(LatestInt)
                LatestInt+=1
                addTemplateToList() } },
        ) {
            Icon(Icons.Outlined.Add, contentDescription ="AddTimeChunk")
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
}


@Composable
fun EditColumn(Number:Int = 0){
    val valueList = mutableListOf(1,2,3,4,5,6,7)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.DateRange,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Column")
        //SimpleOutlinedTextField(Label = "Column$Number", content = "")
        SelectTime(Label = "Column", Number)
    }
}

@Composable
fun EditStartingTime(Number: Int = 0){
    val valueList = mutableListOf(1,2,3,4,5,6,7,8,9,10,11,12,13)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.Info,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "StartingTime")
        //SimpleOutlinedTextField(Label = "StartingTime$Number", content = "")
        SelectTime(Label = "StartingTime", Number)
    }
}

@Composable
fun EditEndingTime(Number: Int = 0){
    val valueList = mutableListOf(1,2,3,4,5,6,7,8,9,10,11,12,13)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier
            .width(35.dp)
            .height(35.dp),)
        //SimpleOutlinedTextField(Label = "EndingTime$Number", content = "")
        SelectTime(Label = "EndingTIme", Number)
    }
}

@Composable
fun EditLocation(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.LocationOn,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Location")
        SimpleOutlinedTextField(Label = "Location", content = "")
    }
}


fun changeData(type: String, content: String) {
    val operate : String
    var num = 0

    if (Regex("[A-Za-z]+[0-9]").containsMatchIn(type)) {
        num = type.last().code - 48
        operate = type.take(type.length - 1)
    }
    else
        operate = type

    Log.d("message1", operate.toString())
    Log.d("message2", content.toString())
    Log.d("message3", num.toString())
//    when (operate) {
//        "Name" -> course.Name = content
//        "Location" -> course.Location = content
//        "StartingTime" -> templateList[num].StartingTime = content.toLong() - 1
//        "EndingTime" -> templateList[num].EndingTime = content.toLong() - 1
//        "Column" -> templateList[num].Column = content.toLong() - 1
//    }
}

fun saveData(context: Context) {
    val activity = context as MainActivity
    val schedule = activity.schedule
    schedule.addCourse(course)
    templateList.clear()
    course = CourseInfo("Name", 0, 0, templateList, "Prompt", "Location")
}

fun addTemplateToList(){
    val template = CourseTemplate(0, 0, 0, 1)
    template.info = course
    templateList.add(template)
}

fun removeTemplateFromList(Number:Int){
    try {
        templateList.removeAt(Number)
    }
    finally {
    }
}


