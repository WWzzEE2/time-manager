package com.example.myapplication.backstage

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.backstage.CourseInfo
import com.google.android.material.composethemeadapter.sample.backstage.CourseTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException


data class UserAccount(
    // future to use
    var UserName: String,
    var ID: String,
    var PassWord: String,
    // Login Status to add
)

class Import {

    private var defaultHeaders =  Headers.Builder()
    .add("Accept","application/json, text/javascript, */*; q=0.01")
    .add("Accept-Encoding", "gzip, deflate, br")
    .add("Accept-Language", "en-US,en;q=0.9")
    .add("Host", "iaaa.pku.edu.cn")
    .add("Origin","https://iaaa.pku.edu.cn")
    .add("Connection","keep-alive").build()

    private val homeUrl="https://iaaa.pku.edu.cn/iaaa/oauth.jsp"
    private val loginUrl="https://iaaa.pku.edu.cn/iaaa/oauthlogin.do"
    private val redirUrl="https://course.pku.edu.cn/webapps/bb-sso-bb_bb60/execute/authValidate/campusLogin"

    //DEBUG:your account here
    private val user = UserAccount("","1","")
    //DEBUG:your account here

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
            Log.d("TestdataName",name.toString())
            var course = CourseInfo(name,0,0,
                emptyList<CourseTemplate>().toMutableList(),"Prompt", "Location")
            var templateList = mutableStateListOf<CourseTemplate>()

            var startingTime:Long=0
            var endingTime:Long=0

            val timeAndLocationSplit = split[10*i+7].split("\n")
            for (info in timeAndLocationSplit) {
                if (info.startsWith("考试时间") or info.startsWith("考试方式")) {
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
                var rowEnd=infoSplit[4].toLong() + 1
                Log.d("rowStart", rowStart.toString())
                Log.d("rowEnd", rowEnd.toString())
                templateList.add(CourseTemplate(column,rowStart, rowEnd,1))
                var k:Int=0
                for (s in infoSplit) {
                    Log.d("TestdataSplit", s.trimIndent()+" "+ k.toString())
                    k++
                }

            }
            course.startingTime=startingTime
            course.endingTime=endingTime
            course.addTemplate(templateList)




            for (s in templateList)
                Log.d("TestdataTemplateList", s.toString())

            Log.d("TestdataCourse", course.toString())
            if (!schedule.addCourse(course)) {
                Log.d("TestdataCourse","add failed")
                return false
            }
            Log.d("TestdataCourse", "add successfully")

        }
        return true
    }

    private fun visitIAAA() {
        val homeHeaders = Headers.Builder().addAll(defaultHeaders)
            .add("Referer","https://course.pku.edu.cn/")
            .add("Upgrade-Insecure-Requests","1").build()
        val formBody = FormBody.Builder()
            .add("appID", "blackboard")
            .add("appName","1")
            .add("redirectUrl",redirUrl).build()
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .headers(homeHeaders).url(homeUrl).post(formBody).build()
        val call = okHttpClient.newCall(request)

        try {
            var response = call.execute()
            if (response.isSuccessful) {
                Log.d("TestWebLogin", response.body!!.string())
            }
        } catch (e:IOException) {
            e.printStackTrace()
        }
    }

    private fun loginIAAA() {
        val loginHeaders = Headers.Builder().addAll(defaultHeaders)
            .add("Referer", homeUrl)
            .add("X-Requested-With","XMLHttpRequest").build()
        val formBody=FormBody.Builder()
            .add("appid","blackboard")
            .add("userName",user.UserName)
            .add("password",user.PassWord)
            .add("randCode","")
            .add("smsCode","")
            .add("otpCode","")
            .add("redirUrl",redirUrl).build()
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .headers(loginHeaders).url(loginUrl).post(formBody).build()
        val call = okHttpClient.newCall(request)

        try {
            var response = call.execute()
            if (response.isSuccessful) {
                Log.d("TestWebLogin", response.body!!.string())
            }
        } catch (e:IOException) {
            e.printStackTrace()
        }
    }

    fun importFromCourse() {
        GlobalScope.launch {
            visitIAAA()
            loginIAAA()
        }
    }

}