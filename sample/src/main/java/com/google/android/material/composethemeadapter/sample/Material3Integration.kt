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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.example.myapplication.backstage.Schedule
import com.example.myapplication.backstage.TestDataConfig
import com.example.myapplication.front.BottomNavigation
import com.google.android.material.color.DynamicColors
import com.google.android.material.composethemeadapter3.Mdc3Theme

class Material3IntegrationActivity : AppCompatActivity() {
    lateinit var schedule: Schedule
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)

        val contentView = ComposeView(this)
        setContentView(contentView)

        var config = TestDataConfig(20,1,5,12)

        schedule = Schedule(this, config)
        contentView.setContent {
            Mdc3Theme {
                Greeting()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    BottomNavigation()
}

