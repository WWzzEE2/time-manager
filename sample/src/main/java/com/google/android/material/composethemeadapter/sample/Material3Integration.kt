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
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.example.myapplication.backstage.Import
import com.example.myapplication.backstage.Schedule
import com.example.myapplication.backstage.TestDataConfig
import com.example.myapplication.front.BottomNavigation
import com.google.android.material.color.DynamicColors
import com.google.android.material.composethemeadapter3.Mdc3Theme

class MainActivity : AppCompatActivity() {
    lateinit var schedule: Schedule
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)

        val contentView = ComposeView(this)
        setContentView(contentView)

        var config = TestDataConfig(20,1000,20,12)

        schedule = Schedule(this, config)
        contentView.setContent {
            Mdc3Theme {
                Greeting()
            }
        }

        var test = Import()
        val testdata = """
            学生网上选课 >> 查看选课结果： 【信息科学技术学院 陈萧白】
            
            课程名	课程类别	学分	周学时	教师	班号	开课单位	教室信息	选课结果	IP地址	操作时间
            概率统计 （A）	专业必修	3.0	3.0	章复熹(副教授)	1	数学科学学院	1~16周 每周周五3~4节 二教205
            1~16周 单周周三7~8节 二教205
            8~8周 每周周五3~4节 二教105
            考试时间：20221223上午；	已选上	125.115.42.228	2022-07-07 12:22:13
            计算机组织与体系结构	专业必修	3.0	3.0	张杰(助理教授)	2	信息科学技术学院	1~15周 每周周四7~9节 三教203(备注：中英双语授课)
            考试时间：20221229下午；	已选上	125.115.42.228	2022-07-07 14:31:56
            音乐与数学	通选课(通识核心课III)	2.0	2.0	王杰(教授)	1	数学科学学院	1~15周 每周周一10~11节 理教108	已选上	10.7.5.187	2022-09-02 18:30:25
            射箭	全校必修	1.0	2.0	张冰(教学副教授)	2	体育教研部	1~15周 每周周二7~8节(备注：一体)
            考试方式：堂考、论文、或统一时间考试	已选上	10.129.196.86	2022-09-01 17:13:08
            公共基础日语（一）	全校任选	4.0	6.0	刘佳,张越	1	外国语学院	1~15周 每周周一10~11节 三教101
            1~15周 每周周五10~11节 三教101
            1~15周 每周周三10~11节 三教101	未选上	10.7.5.187	2022-09-01 20:24:48
            公共基础日语（一）	全校任选	4.0	6.0	王瑞阳,李梦佳	3	外国语学院	1~15周 每周周二10~12节 三教103
            1~15周 每周周四10~12节 三教103
            考试方式：堂考、论文、或统一时间考试	未选上	10.7.5.187	2022-09-02 18:30:15
            软件工程	专业必修	4.0	5.0	孙艳春(副教授)	1	信息科学技术学院	1~15周 每周周四5~6节 二教316
            1~15周 每周周二3~4节 二教316
            考试时间：20221220上午；	已选上	10.7.16.6	2022-09-01 17:07:58
            计算机网络	专业必修	4.0	5.0	黄群(助理教授)	1	信息科学技术学院	1~15周 每周周三1~2节 理教207
            1~15周 每周周一5~6节 理教207
            考试时间：20221219下午；	已选上	125.115.42.228	2022-07-07 14:31:32
            操作系统	专业必修	4.0	5.0	陈向群(教授)	1	信息科学技术学院	1~15周 每周周三3~4节 二教205
            1~15周 每周周一7~8节 二教205
            考试时间：20221226下午；	已选上	125.115.42.228	2022-07-07 12:22:55
        """.trimIndent()
        test.importFromElective(testdata, this)
        Log.d("Testdata","TestDone")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    BottomNavigation()
}

