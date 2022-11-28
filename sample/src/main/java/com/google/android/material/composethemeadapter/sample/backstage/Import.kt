package com.example.myapplication.backstage

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.backstage.CourseInfo
import com.google.android.material.composethemeadapter.sample.backstage.CourseTemplate


data class UserAccount( // future to use
    var UserName: String,
    var ID: String,
    var PassWord: String,
    // Login Status to add
)

class Import {
    /**
     * Add a course with data pasted from elective website
     * @return False when add fail
     */
    fun importFromElective(data: String, context: Context): Boolean {
        val activity = context as MainActivity
        val schedule = activity.schedule
        Log.d("Testdata", data)
        val split = data.split("\t")
        val splitLen = split.size
        var startIndex = -2
        for (i in 0 until splitLen) {
            if (split[i] == "课程类别") {
                Log.d("TestdataSplitStart", i.toString())
                startIndex = i - 1
                break
            }
        }
        if (startIndex == -2) {
            return false
        }
        val courseNum = (splitLen - startIndex - 1) / 10 - 1
        for (i in 1..courseNum) {
            //Course i: split[10*i .. 10*i+9]
            if (split[10*i+8]=="未选上")
                continue
            val name = split[10*i].split("\n")[1]
            var course = CourseInfo(name,0,0,
                emptyList<CourseTemplate>().toMutableList(),"Prompt", "Location")
            var templateList = mutableStateListOf<CourseTemplate>()
            // copy from front/calendar/edit.kt
            //course.StartingTime = schedule.termStartTime
            //course.EndingTime = schedule.termStartTime + 1000L * 3600 * 24 * 7 * 20

            val timeAndLocationSplit = split[10*i+7].split("\n")
            for (info in timeAndLocationSplit) {
                if (info.startsWith("考试时间")) {
                    break
                }
                Log.d("TestdataBeforeSplit",info)
                val infoSplit = info.split("~", "周 ", "周周", "节")
                for (s in infoSplit) {
                    Log.d("TestdataSplit", s.trimIndent())
                }
                //TODO: 使用infoSplit中的信息填充course、templateList，最后调用addCourse
                //      Split具体内容可看Logcat搜Testdata，原data在Material3Integration.kt中

            }
        }
        return true
    }
}