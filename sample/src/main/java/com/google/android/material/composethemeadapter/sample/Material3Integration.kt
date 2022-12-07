/* * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.*/


package com.google.android.material.composethemeadapter.sample

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.example.myapplication.backstage.Import
import com.example.myapplication.backstage.UserAccount
import com.example.myapplication.front.BottomNavigation
import com.google.android.material.color.DynamicColors
import com.google.android.material.composethemeadapter.sample.backstage.*
import com.google.android.material.composethemeadapter.sample.backstage.getWeekDay
import com.google.android.material.composethemeadapter.sample.widget.TimeManagerWidgetProvider
import com.google.android.material.composethemeadapter3.Mdc3Theme
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var schedule: Schedule
    lateinit var testdata:String

    var account = UserAccount(
        "2000013161",
        "2000013161",
        "nbyqy123"
    )

    object GlobalInformation {
        @JvmStatic
        lateinit var activity: MainActivity
            internal set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalInformation.activity = this

        DynamicColors.applyToActivityIfAvailable(this)

        val contentView = ComposeView(this)
        setContentView(contentView)

        var config = TestDataConfig(20,1000,20,12)
        schedule = Schedule(this)


        var test = Import()
        test.importFromCourse()

        contentView.setContent {
            Mdc3Theme {
                BottomNavigation(this)
            }
        }


    }


}



