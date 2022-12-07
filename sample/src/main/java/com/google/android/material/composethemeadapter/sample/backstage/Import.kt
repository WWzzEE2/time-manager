package com.example.myapplication.backstage

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.material.composethemeadapter.sample.MainActivity
import com.google.android.material.composethemeadapter.sample.backstage.CourseInfo
import com.google.android.material.composethemeadapter.sample.backstage.CourseTemplate
import com.google.android.material.composethemeadapter.sample.backstage.DDlInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet


data class UserAccount(
    // future to use
    var userName: String,
    var id: String,
    var passWord: String
    // Login Status to add
) {
    var token = ""
}

class storeCookieJar:CookieJar {
    private var cookieStore: HashMap<String?, List<Cookie>> = HashMap()
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieStore[url.host]
        return cookies ?: ArrayList()
    }
}

class Import {

    private val iaaaHeaders =  Headers.Builder()
        .add("Accept","application/json, text/javascript, */*; q=0.01")
        .add("Accept-Encoding", "gzip, deflate, br")
        .add("Accept-Language", "en-US,en;q=0.9")
        .add("Host", "iaaa.pku.edu.cn")
        .add("Origin","https://iaaa.pku.edu.cn")
        .add("Connection","keep-alive").build()

    private val courseHeader = Headers.Builder()
        .add("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        //.add("Accept-Encoding","gzip, deflate, br")
        .add("Accept-Language", "en-US,en;q=0.9")
        .add("Host","course.pku.edu.cn")
        .add("Upgrade-Insecure-Requests","1")
        .add("Connection","keep-alive")
        .add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
        .add("Refer","https://iaaa.pku.edu.cn/")
        .build()

    private val ddlHeader =  Headers.Builder()
        .add("Accept", "*/*")
        .add("Accept-Encoding", "gzip, deflate, br")
        .add("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")
        .add("Connection","keep-alive")
        .add("Host", "course.pku.edu.cn")
        .add("Referer", "https://course.pku.edu.cn/webapps/calendar/viewPersonal")
        .add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
        .build()

    private val homeUrl="https://iaaa.pku.edu.cn/iaaa/oauth.jsp"
    private val loginUrl="https://iaaa.pku.edu.cn/iaaa/oauthlogin.do"
    private val ssoUrl="https://course.pku.edu.cn/webapps/bb-sso-bb_bb60/execute/authValidate/campusLogin"

    private val cookieJar = storeCookieJar()

    //DEBUG:your account here
    private val user = MainActivity.GlobalInformation.activity.account
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
                var period:Long=0
                when(infoSplit[2][0]){
                    '每'->period=0
                    '单'->period=2
                    '双'->period=1
                }
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
                endingTime=schedule.getWeekStamp(infoSplit[1].toLong()+1)
                var rowStart=infoSplit[3].substring(1).toString().toLong()-1
                var rowEnd=infoSplit[4].toLong()
                Log.d("rowStart", rowStart.toString())
                Log.d("rowEnd", rowEnd.toString())
                templateList.add(CourseTemplate(column,rowStart, rowEnd,period))
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

    private fun setToken(jsonData:String):Boolean {
        val mapper = jacksonObjectMapper()
        try {
            val jsonNode = mapper.readTree(jsonData)
            val state = jsonNode.get("success")
            if (state.toString()=="false")
                return false
            val tokenNode = jsonNode.get("token")
            user.token = tokenNode.textValue()
            Log.d("TestWebLogin", "token get: ${user.token}")
        } catch (e:Exception) {
            Log.d("TestWebToken","ERR, jsonData: $jsonData")
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun visitCourse():Boolean {
        val courseUrl = "https://course.pku.edu.cn"
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val request = Request.Builder()
            .headers(courseHeader).url(courseUrl).get().build()
        val call = okHttpClient.newCall(request)
        try {
            var response = call.execute()
            if (response.isSuccessful) {
                Log.d("TestWebCourse", response.body!!.string())
            }
            else {
                Log.d("TestWebCourse", "code: ${response.code}")
                return false
            }
        } catch (e:IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun visitIAAA():Boolean {
        val homeHeaders = Headers.Builder().addAll(iaaaHeaders)
            .add("Referer","https://course.pku.edu.cn/")
            .add("Upgrade-Insecure-Requests","1").build()
        val formBody = FormBody.Builder()
            .add("appID", "blackboard")
            .add("appName","1")
            .add("redirectUrl",ssoUrl).build()
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val request = Request.Builder()
            .headers(homeHeaders).url(homeUrl).post(formBody).build()
        val call = okHttpClient.newCall(request)

        try {
            var response = call.execute()
            if (response.isSuccessful) {
                Log.d("TestWebIAAA", response.body!!.string())
            }
            else {
                Log.d("TestWebIAAA", "code: ${response.code}")
                return false
            }
        } catch (e:IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun loginIAAA():Boolean {
        val loginHeaders = Headers.Builder().addAll(iaaaHeaders)
            .add("Referer", homeUrl)
            .add("X-Requested-With","XMLHttpRequest").build()
        val formBody=FormBody.Builder()
            .add("appid","blackboard")
            .add("userName",user.userName)
            .add("password",user.passWord)
            .add("randCode","")
            .add("smsCode","")
            .add("otpCode","")
            .add("redirUrl",ssoUrl).build()
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val request = Request.Builder()
            .headers(loginHeaders).url(loginUrl).post(formBody).build()
        val call = okHttpClient.newCall(request)

        try {
            var response = call.execute()
            if (response.isSuccessful) {
                val jsonData = response.body!!.string()
                Log.d("TestWebLogin", jsonData)
                if (!setToken(jsonData)) {
                    Log.d("TestWebLogin", "ERROR: cannot get token, data:" + jsonData)
                    return false
                }
            }
            else {
                Log.d("TestWebLogin", "code: ${response.code}")
                return false
            }
        } catch (e:IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun loginSSO():Boolean {
        if (user.token.isEmpty())
            return false
        val getUrl = ssoUrl.toHttpUrlOrNull()!!.newBuilder()
        getUrl.addQueryParameter("_rand", Random.nextFloat().toString())
        getUrl.addQueryParameter("token",user.token)
        Log.d("TestWebSSO","usr: ${getUrl.build()}")
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val request = Request.Builder()
            .headers(courseHeader).url(getUrl.build()).get().build()
        val call = okHttpClient.newCall(request)
        try {
            var response = call.execute()
            if (response.isSuccessful) {
                Log.d("TestWebSSO", response.body!!.string())
            }
            else {
                Log.d("TestWebSSO", "code: ${response.code}")
                return false
            }
        } catch (e:IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun importDDLFromCourse(startTime:Long, endTime:Long): Boolean {
        val url = "https://course.pku.edu.cn/webapps/calendar/calendarData/selectedCalendarEvents"//?start=${startTime}&end=${endTime}&course_id=&mode=personal"
        val getUrl = url.toHttpUrlOrNull()!!.newBuilder()
        getUrl.addQueryParameter("start", startTime.toString())
        getUrl.addQueryParameter("end", endTime.toString())
        getUrl.addQueryParameter("course_id", "")
        getUrl.addQueryParameter("mode", "personal")
        val okHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val request = Request.Builder()
            .headers(ddlHeader).url(getUrl.build()).get().build()
        val call = okHttpClient.newCall(request)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                //Log.d("TestDDLGet", response.body!!.string())
                parseDDlFromNetJson(response.body!!.string())
            }
            else {
                Log.d("TestDDLGet", "code: ${response.code}")
                return false
            }
        } catch (e:IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun parseDDlFromNetJson(json: String?) {
        Log.d("ddlList", "start")
        val objectMapper = jacksonObjectMapper()
        val schedule = MainActivity.GlobalInformation.activity.schedule
        val pulledDDl = schedule.pulledDDl
        val simpleModule = SimpleModule("CustomDDlDeserializer", Version(1,0,0,null,null,null))
        simpleModule.addDeserializer(PackedDDl::class.java, CustomDDlDeserializer())

        objectMapper.registerModule(simpleModule)
        val ddlList = objectMapper.readValue<List<PackedDDl>>(json!!)
        Log.d("ddlList", ddlList.toString())
        for (ddlPack in ddlList) {
            if (!pulledDDl.contains(ddlPack.uid) && ddlPack.ddl != null) {
                pulledDDl.add(ddlPack.uid)
                schedule.addDDl(ddlPack.ddl)
            }
        }

    }

    fun importFromCourse() {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.d("TestWebERR", "catch a coroutine error: $throwable")
        }
        GlobalScope.launch(exceptionHandler) {
            // visit web to get cookie
            if (!visitCourse()) {
                Log.d("TestWebERR","visit course failed")
                return@launch
            }
            // IAAALogin
            if (!visitIAAA()) {
                Log.d("TestWebERR", "visit IAAA failed")
                return@launch
            }
            if (!loginIAAA()) {
                Log.d("TestWebERR","IAAA login failed")
                return@launch
            }
            // SSOLogin + visit main page
            if (!loginSSO()) {
                Log.d("TestWebERR","SSO login failed")
                return@launch
            }

            val schedule = MainActivity.GlobalInformation.activity.schedule
            val term = schedule.termInfo

            importDDLFromCourse(Calendar.getInstance().timeInMillis, term.endingTime)

            schedule.saveAll()
        }
    }

}

data class PackedDDl(val ddl: DDlInfo?, val uid: String)

class CustomDDlDeserializer() : StdDeserializer<PackedDDl>(PackedDDl::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): PackedDDl? {

        if (p != null) {
            val codec = p.codec
            val node = codec.readTree<JsonNode>(p)
            val schedule = MainActivity.GlobalInformation.activity.schedule

            var endDate = node.get("endDate").asText()
            endDate= endDate.substring(0,10)+" "+endDate.substring(11,19)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = simpleDateFormat.parse(endDate)
            var timestamp = date?.time
            timestamp = timestamp?.plus(8L*3600*1000)

            return PackedDDl(
                if(timestamp != null) DDlInfo(node.get("calendarName").asText(), System.currentTimeMillis(), timestamp, node.get("title").asText(), schedule.termInfo.startingTime) else null,
                node.get("id").asText()
            )
        }
        else
            throw NotImplementedError("No parser")
    }
}