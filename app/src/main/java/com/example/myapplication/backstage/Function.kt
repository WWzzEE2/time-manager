package com.example.myapplication.backstage

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
    val week: Short,
    val day:Short
)

internal fun getWeekDay(termStart: Long, timeStamp: Long) : WeekDay {
    val week = (timeStamp - termStart) / 24 / 3600 / 1000 / 7
    return Calendar.getInstance().let {
        it.time = Date(timeStamp)
        WeekDay(week.toShort(), (it[Calendar.DAY_OF_WEEK]-1).toShort())
    }
}

internal fun getWeek(termStart: Long, timeStamp: Long) = ((timeStamp - termStart) / 24 / 3600 / 1000 / 7).toShort()

fun getDay(timeStamp: Long) = Calendar.getInstance().let {
    it.time = Date(timeStamp)
    (it[Calendar.DAY_OF_WEEK]-1).toShort()
}

fun getHour(timeStamp: Long) = Calendar.getInstance().let {
    it.time = Date(timeStamp)
    (it[Calendar.HOUR_OF_DAY].toShort())
}

fun getMin(timeStamp: Long): Long {
    //输入时间戳，返回位于一天中的第几分钟
    val start_day=(timeStamp/24/3600/1000)*24*3600*1000
    val past_min=(timeStamp-start_day)/1000/60
    return past_min
}

fun getRow(timeStamp: Long):Short{
    //输入时间戳，返回属于哪一列
    val min=getMin(timeStamp)
    var column=-1
    for(index in 1..RowStart.size)
        if(min>= RowStart.get(index)!!&&min< RowEnd.get(index)!!)
        {
            column=index
            break
        }
    return column.toShort()
}