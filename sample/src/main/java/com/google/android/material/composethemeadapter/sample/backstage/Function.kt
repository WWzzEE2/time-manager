package com.example.myapplication.backstage

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal fun min(a: Short, b: Short) = if (a < b) a else b
internal tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) abs(a) else gcd(b, a % b)
internal tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) abs(a) else gcd(b, a % b)
internal fun crossover(s1: Int, e1: Int, s2: Int, e2: Int) = max(s1, s2) < min(e1, e2)
internal fun crossover(s1: Long, e1: Long, s2: Long, e2: Long) = max(s1, s2) < min(e1, e2)

data class WeekDay (
    var week: Long,
    var day:Long
)

internal fun getWeekDay(termStart: Long, timeStamp: Long) : WeekDay {
    val week = (timeStamp - termStart) /( 24 * 3600 * 1000 * 7)  + 1
    val day= ((timeStamp-termStart)/(1000*3600*24))%7
    return WeekDay(week,day)
}

internal fun getWeek(termStart: Long, timeStamp: Long) = ((timeStamp - termStart) / 24 / 3600 / 1000 / 7)

fun getDay(timeStamp: Long) = Calendar.getInstance().let {
    it.time = Date(timeStamp)
    (it[Calendar.DAY_OF_WEEK]-1).toLong()
}

fun getHour(timeStamp: Long) :Long{
    val pasthour=((timeStamp- termInfo.StartingTime)/1000/3600)%24
    return pasthour
}

fun getPastMin(timeStamp: Long): Long {
    //输入时间戳，返回位于一天中的第几分钟
    val past_min=((timeStamp- termInfo.StartingTime)/1000/60)%(24*60)
    return past_min
}

fun getRow(timeStamp: Long):Short{
    //输入时间戳，返回属于哪一列
    val past_min=getPastMin(timeStamp)
    var column=-1
    for(index in 1..termInfo.RowStart.size)
        if(past_min>= termInfo.RowStart.get(index)!!&&past_min< termInfo.RowEnd.get(index)!!)
        {
            column=index
            break
        }
    return column.toShort()
}

fun getPastMin(hour: Short, min:Short):Long{
    //输入小时和分钟，返回位于当天多少分钟
    val past_min=(hour*60+min).toLong()
    return past_min
}

fun getTimeStamp(year: Long,month: Long,day: Long): Long {
    var dateTime:String = ""
    dateTime += year.toString()
    dateTime+=("-")
    if(month<10)
        dateTime+=("0")
    dateTime+=(month.toString())
    dateTime+=("-")
    if (day<10)
        dateTime+=(0)
    dateTime+=(day.toString())

    dateTime+=(" 00:00:01")
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = simpleDateFormat.parse(dateTime)
    val timestamp = date?.time
    return timestamp!!.toLong()
}