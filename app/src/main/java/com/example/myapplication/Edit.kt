package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.myapplication.backstage.CourseInfo
import com.example.myapplication.backstage.CourseTemplate
import kotlin.collections.ArrayList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPage(){
    Scaffold(topBar = {ChangeStat()}) {
        EditDetail()
    }
}

@Composable
fun ChangeStat(){
    SmallTopAppBar(
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = { Text("Edit")},
        actions = {
            IconButton(onClick = {

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
        EditFrequency()
        EditStartingTime()
        EditEndingTime()
        EditLocation()
    }
}

@Composable
fun SimpleOutlinedTextField(Label:String, content:String) {
    var text by rememberSaveable { mutableStateOf(content) }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(Label) },
        modifier = Modifier.testTag("OutlinedTextField")
    )
    SaveData(Label, text)
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
fun EditFrequency(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.DateRange,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Frequency")
        SimpleOutlinedTextField(Label = "Frequency", content = "")
    }
}

@Composable
fun EditStartingTime(){
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
        SimpleOutlinedTextField(Label = "StartingTime", content = "")
    }
}

@Composable
fun EditEndingTime(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.Info,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "EndingTime")
        SimpleOutlinedTextField(Label = "EndingTime", content = "")
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


fun SaveData(type:String, content: String){
    var name : String
    var column : Long
    var startingtime : Long
    var endingtime : Long
    var period : Long
    var location
    var course = CourseInfo("Name", 0, 0, ArrayList(), "prompt", "location")
    var template = CourseTemplate(0, 0, 0, 1)
    template.info = course
    when(type) {
        "Name" -> name = content
        "Frequency" -> period = content.toLong()
        "Startingtime" -> template.StartingTime = content.toLong()
        "Endingtime" -> template.EndingTime = content.toLong()
        "Location" -> course.Location = content
        "Save" ->
    }
}


