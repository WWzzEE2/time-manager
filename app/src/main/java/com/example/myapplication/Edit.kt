package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.myapplication.backstage.CourseInfo
import com.example.myapplication.backstage.CourseTemplate
import kotlin.collections.ArrayList

@Preview
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
        EditLocation()
        EditTimeChunk()

    }
}

@Composable
fun SimpleOutlinedTextField(Label:String, content:String) {
    var text by rememberSaveable { mutableStateOf(content) }
    Column() {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                saveData(Label, text) },
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
    var isExpand by remember {
        mutableStateOf(false)
    }
    var t = false
    Column(

    ) {
        EditColumn()
        EditStartingTime()
        EditEndingTime()
        Button(onClick = { isExpand = !isExpand },
            enabled = true,
            modifier = Modifier.size(30.dp),
            shape= RoundedCornerShape(8.dp),
        ) {
            Text(text = "+", fontSize = 10.em)
        }
        if (isExpand != t) {
            EditColumn()
            EditStartingTime()
            EditEndingTime()
            t = isExpand
        }
    }

}

@Composable
fun EditColumn(){
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
        SimpleOutlinedTextField(Label = "Column", content = "")
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
        Spacer(modifier = Modifier
            .width(35.dp)
            .height(35.dp),)
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

fun saveData(type:String, content: String){
    var name = ""; var column : Long  = 0; var startingTime : Short = 0
    var endingTime : Short  = 0; var period : Long  = 1; var location  = ""
    when(type) {
        "Name" -> name = content
        "Column" -> column = content.toLong()
        "StartingTime" -> startingTime = content.toShort()
        "EndingTime" -> endingTime = content.toShort()
        "Location" -> location = content
        "Save" -> {
            var course = CourseInfo(name, 0, 0, ArrayList(), "prompt", location)
            var template = CourseTemplate(column, startingTime, endingTime, 1)
        }
    }

}
