package com.example.myapplication.backstage

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.backstage.CourseInfo
import com.google.android.material.composethemeadapter.sample.backstage.CourseTemplate
import com.google.android.material.composethemeadapter.sample.backstage.getWeekStamp


data class account( // future to use
    var UserName: String,
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

        for (c in schedule.getAllCourse())
            schedule.removeCourse(c)

        val courseNum = (splitLen - startIndex - 1) / 10 - 1
        for (i in 1..courseNum) {
            //Course i: split[10*i .. 10*i+9]
            if (split[10*i+8]=="未选上")
                continue
            val name = split[10*i].split("\n")[1]
            Log.d("name",name.toString())
            var course = CourseInfo(name,0,0,
                emptyList<CourseTemplate>().toMutableList(),"Prompt", "Location")
            var templateList = mutableStateListOf<CourseTemplate>()
            // copy from front/calendar/edit.kt
            //course.StartingTime = schedule.termStartTime
            //course.EndingTime = schedule.termStartTime + 1000L * 3600 * 24 * 7 * 20
            var startingTime:Long=0
            var endingTime:Long=0

            val timeAndLocationSplit = split[10*i+7].split("\n")
            for (info in timeAndLocationSplit) {
                if (info.startsWith("考试时间")) {
                    break
                }
                Log.d("TestdataBeforeSplit",info)
                val infoSplit = info.split("~", "周 ", "周周", "节")
                val location=infoSplit[infoSplit.size-1]
                course.location=location
                if(infoSplit.size<=3)
                    continue
                var column:Long=0
                when(infoSplit[3][0])
                {
                    '一'->column=0
                    '二'->column=1
                    '三'->column=2
                    '四'->column=3
                    '五'->column=4
                    '六'->column=5
                    '日'->column=6
                }
                startingTime= schedule.getWeekStamp(infoSplit[0].toLong())
                endingTime=schedule.getWeekStamp(infoSplit[1].toLong())
                var rowStart=infoSplit[3].substring(1).toString().toLong()
                var rowEnd=infoSplit[4].toLong()
                Log.d("rowStart", rowStart.toString())
                Log.d("rowEnd", rowEnd.toString())
                templateList.add(CourseTemplate(column,rowStart, rowEnd,1))
                var k:Int=0
                for (s in infoSplit) {
                    Log.d("TestdataSplit", s.trimIndent()+" "+ k.toString())
                    k++
                }
                //TODO: 使用infoSplit中的信息填充course、templateList，最后调用addCourse
                //      Split具体内容可看Logcat搜Testdata，原data在Material3Integration.kt中

            }
            course.startingTime=startingTime
            course.endingTime=endingTime
            course.addTemplate(templateList)




            for (s in templateList)
                Log.d("TestdataTemplateList", s.toString())

            Log.d("TestdataCourse", course.toString())
            schedule.addCourse(course)
            Log.d("TestdataCourse", "add successfully")

        }
        return true
    }
}