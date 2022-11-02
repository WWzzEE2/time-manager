package com.example.myapplication

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.myapplication.backstage.CourseInfo
import com.example.myapplication.backstage.CourseTemplate
import kotlin.collections.ArrayList

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
    val maxLength = 5
    if (Regex("[A-Za-z]+[0-9]").containsMatchIn(Label))
    Column() {
        OutlinedTextField(
            value = text,
            onValueChange = {
                if (it.length < maxLength){
                text = it
                changeData(Label, text) }
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
    var expandTimeChunk by remember {
        mutableStateOf(0)
    }
    Column(
    ) {
        addTemplateToList()
        EditColumn()
        EditStartingTime()
        EditEndingTime()
        IconButton(
            onClick = {
                expandTimeChunk += 1
                addTemplateToList() },
        ) {
            Icon(Icons.Outlined.Add, contentDescription ="AddTimeChunk")
        }
        LazyColumn() {
            for (i in 1..expandTimeChunk) {
                item { EditColumn(i.toString()) }
                item { EditStartingTime(i.toString()) }
                item { EditEndingTime(i.toString()) }
                item {
                    IconButton(
                        onClick = {
                            if (expandTimeChunk > 0) expandTimeChunk -= 1
                            removeTemplateFromList(i.toString())
                        },
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription ="AddTimeChunk")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
   }
}

@Composable
fun EditColumn(Number:String = "0"){
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
        SimpleOutlinedTextField(Label = "Column$Number", content = "")
    }
}

@Composable
fun EditStartingTime(Number: String = "0"){
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
        SimpleOutlinedTextField(Label = "StartingTime$Number", content = "")
    }
}

@Composable
fun EditEndingTime(Number: String = "0"){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier
            .width(35.dp)
            .height(35.dp),)
        SimpleOutlinedTextField(Label = "EndingTime$Number", content = "")
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


private var templateList = mutableListOf<CourseTemplate>()
private var course = CourseInfo("Name", 0, 0, templateList, "Prompt", "Location")
fun changeData(type: String, content: String) {
    val operate : String
    var num = 0
    if (Regex("[A-Za-z]+[0-9]").containsMatchIn(type)) {
        num = type.last().code - 48
        operate = type.take(type.length - 1)
    }
    else
        operate = type
    when (operate) {
        "Name" -> course.Name = content
        "Location" -> course.Location = content
        "StartingTime" -> templateList[num].StartingTime = content.toLong()
        "EndingTime" -> templateList[num].EndingTime = content.toLong()
        "Column" -> templateList[num].Column = content.toLong()
    }

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

fun removeTemplateFromList(Number:String){
    templateList.removeAt(Number.toInt())
}
