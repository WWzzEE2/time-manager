package com.example.myapplication

import android.annotation.SuppressLint
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

import com.example.myapplication.front.*
import com.example.myapplication.front.calendar.CenterText
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.google.android.material.composethemeadapter.sample.R
import com.google.android.material.composethemeadapter.sample.MainActivity


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(context: Context) {
    Scaffold(
        topBar = { TopBar() },
    ) {
        Column(
            Modifier.width(500.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.padding(65.dp, 40.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.beaver),
                        contentDescription = "Contact profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            // Set image size to 40 dp
                            .size(120.dp)
                            // Clip image to be shaped as a circle
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    EditUserName(context)
                    //Spacer(modifier = Modifier.height(1.dp))
                    EditID(context)
                    //Spacer(modifier = Modifier.height(1.dp))
                    EditPassword(context)
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        title = {
            Text("Settings")
        },
        actions = {
//            IconButton(onClick = { /*TODO*/ }) {
//                Icon(Icons.Filled.Settings, contentDescription = "Localized description")
//            }
        }
    )
}

// 缓存页面中修改时的course和template信息，当点击保存按钮时把修改的信息保存到后台
//private var templateList = mutableStateListOf<CourseTemplate>()
//private var course =
//    CourseInfo("Name", 0, 0, emptyList<CourseTemplate>().toMutableList(), "Prompt", "Location")


@ExperimentalMaterial3Api
@Composable
fun SimpleText(Label: String, content: String, context: Context) {
    var text by rememberSaveable { mutableStateOf(content) }
        Column() { OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                changeUserInfo(Label, text, context)
            },
            label = { Text(Label) },
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun SimplePasswordText(Label: String, content: String, context: Context) {

    var password by rememberSaveable { mutableStateOf(content) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = {
            password = it
            changeUserInfo(Label, password, context)
        },
        label = { Text("Password") },
        singleLine = true,
        placeholder = { Text("Password") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = {passwordVisible = !passwordVisible}){
                Icon(imageVector  = image, description)
            }
        }
    )

//    var text by rememberSaveable { mutableStateOf(content) }
//    Column() { OutlinedTextField(
//        value = text,
//        onValueChange = {
//            text = it
//            changeUserInfo(Label, content, context)
//        },
//        label = { Text(Label) },
//        visualTransformation = {
//            if (passwordVisible)
//                VisualTransformation.None
//            else
//                PasswordVisualTransformation
//        }
//            )
//        }
//    }
}

@ExperimentalMaterial3Api
@Composable
fun EditUserName(context: Context) {
    var activity = context as MainActivity
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Name"
        )
        SimpleText(Label = "Name", content = activity.account.userName, context)
    }
}

@ExperimentalMaterial3Api
@Composable
fun EditID(context: Context) {
    var activity = context as MainActivity
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.Star,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Location"
        )
        SimpleText(Label = "ID", content = activity.account.id, context)
    }
}

@ExperimentalMaterial3Api
@Composable
fun EditPassword(context: Context) {
    var activity = context as MainActivity
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.Lock,
            modifier = Modifier
                .width(35.dp)
                .height(35.dp),
            contentDescription = "Location"
        )
        SimplePasswordText(Label = "Password", content = activity.account.passWord, context)
    }
}

fun changeUserInfo(type: String, content: String, context: Context) {
    var activity = context as MainActivity
    when (type) {
        "Name" -> activity.account.userName = content
        "ID" -> activity.account.id = content
        "Password" -> activity.account.passWord = content
    }
    val schedule = activity.schedule
    schedule.saveAll()
}



