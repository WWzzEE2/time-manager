package com.google.android.material.composethemeadapter.sample.front.calendar

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.myapplication.backstage.Import
import com.example.myapplication.front.ScreenState
import com.example.myapplication.front.calendar.CenterText
import com.example.myapplication.ui.theme.courseBlockColor
import com.google.android.material.composethemeadapter.sample.MainActivity

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadPage(currentState: ScreenState) {
    Scaffold(topBar = { TopAppBar(title = { Text(text = "Load from text") }) }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                var enabled by rememberSaveable() { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(100.dp))
                LoadText(
                    modifier = Modifier
                        .height(450.dp)
                        .align(Alignment.CenterHorizontally),
                    content = ""
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    enabled = enabled,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = courseBlockColor.getColor()),
                    contentPadding = PaddingValues(10.dp),
                    elevation = ButtonDefaults.buttonElevation(2.dp, 1.dp, 2.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(50.dp)
                        .width(100.dp),
                    onClick = {

                        enabled = false
                        var test = Import()
                        test.importFromElective(
                            MainActivity.GlobalInformation.activity.testdata,
                            MainActivity.GlobalInformation.activity
                        )
                        MainActivity.GlobalInformation.activity.schedule.saveAll()
                        currentState.goToCalendar()
                    }) {
                    CenterText(text = "Load")
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun LoadText(content: String, modifier: Modifier) {
    var text by rememberSaveable { mutableStateOf(content) }
    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = {
            text = it
            MainActivity.GlobalInformation.activity.testdata = it
        },
    )
}