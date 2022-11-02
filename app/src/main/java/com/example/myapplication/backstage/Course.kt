package com.example.myapplication.backstage

import java.text.SimpleDateFormat
import java.time.Month
import java.time.Year
import java.util.*

data class TermInfo (
    var StartingTime: Long=0,
    var RowStart : MutableList<Long> = mutableListOf(),
    var RowEnd : MutableList<Long> =mutableListOf()
) {
    fun setTime(year: Long,month: Long,day: Long)
    {
        StartingTime= getTimeStamp(year, month, day)
    }
}
var termInfo:TermInfo=TermInfo()

data class DDlInfo(
    val Name:  String,                  //DDL名字
    val Id : Long,
    val EndingTime : Long,              //DDL结束时间
    val Prompt : String,                //DDL描述
    val StartingTime : Long             //DDL开始工作时间
){
    fun wrapTime(time:Long):String=if (time<10) {"0"+time.toString()} else {time.toString()}
    fun getString():String
    {
        //val weekDay= getWeekDay(termInfo.StartingTime,EndingTime)
        var output =""
        /*output.plus(weekDay.week.toString())
        output.plus("周 ")
        var day_string =""
        when(weekDay.day)
        {
            1.toLong() -> day_string="周一"
            2.toLong() -> day_string="周二"
            3.toLong() -> day_string="周三"
            4.toLong() -> day_string="周四"
            5.toLong() -> day_string="周五"
            6.toLong() -> day_string="周六"
            7.toLong() -> day_string="周日"
        }
        output.plus(day_string)*/
        val hour= getHour(EndingTime)
        val min= getPastMin(EndingTime)-hour*60
        output+=wrapTime(hour)
        output+=":"
        output+=wrapTime(min)
        return output
    }
}

data class CourseTemplate(              //模板，对应以周为单位的日历上的一块\
    val Column: Long,                  //课程位于哪一列
    var StartingTime : Long,           //课程开始于哪一行
    var EndingTime : Long,             //课程结束于哪一行
    val Period: Long                    //一周一次或两周一次(应该不存在三天一次的课吧，
    // 一周两次的话就建立两个template)
) {
    lateinit var info: CourseInfo
}

data class CourseInfo(
    var Name:  String,                  //课程名字
    val StartingTime : Long,            //课程开始时间，直接使用时间戳
    val EndingTime : Long,              //课程结束时间，直接使用时间戳，(前端输入时可以选择持续多少周，但后端不记录)
    val TimeInfo : MutableList<CourseTemplate>,//课程时间，存放CourseTemplate的List
    val Prompt : String,                //课程描述
    val Location: String,               //课程位置
){
    fun addCourse(courseTime:CourseTemplate)
    {
        courseTime.info= this
        TimeInfo.add(courseTime)
    }
}

