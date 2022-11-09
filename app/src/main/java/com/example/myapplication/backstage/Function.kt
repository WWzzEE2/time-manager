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
    val week: Long,
    val day:Long
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

fun getHour(timeStamp: Long, termInfo: TermInfo) :Long{
    val pasthour=((timeStamp- termInfo.startingTime)/1000/3600)%24
    return pasthour
}

fun getPastMin(timeStamp: Long, termInfo: TermInfo): Long {
    //输入时间戳，返回位于一天中的第几分钟
    val past_min=((timeStamp- termInfo.startingTime)/1000/60)%(24*60)
    return past_min
}

/**
 *  输入时间戳，返回属于哪一列
 */
fun getRow(timeStamp: Long, termInfo: TermInfo):Short{
    var column = -1
    val pastMin = getPastMin(timeStamp, termInfo)
    for(index in 1..termInfo.rowStart.size) {
        if (pastMin >= termInfo.rowStart[index] && pastMin < termInfo.rowEnd[index]) {
            column = index
            break
        }
    }
    return column.toShort()
}

/**
 * 输入小时和分钟，返回位于当天多少分钟
 */
fun getPastMin(hour: Long, min: Long): Long = (hour * 60 + min)

fun getTimeStamp(year: Long,month: Long,day: Long): Long {
    var dateTime = ""
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