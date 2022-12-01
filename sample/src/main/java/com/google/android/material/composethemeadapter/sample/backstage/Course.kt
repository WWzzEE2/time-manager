package com.google.android.material.composethemeadapter.sample.backstage

import com.fasterxml.jackson.annotation.JsonIgnore

class TermInfo internal constructor(
    start: Long = 0,
    end: Long = 0,
    rowStart: MutableList<Long> = mutableListOf(),
    rowEnd: MutableList<Long> = mutableListOf()
) {

    var startingTime: Long
        internal set
    var endingTime: Long
        internal set

    var rowStart: MutableList<Long>
    var rowEnd: MutableList<Long>

    init {
        startingTime = start
        endingTime = end
        this.rowStart = rowStart
        this.rowEnd = rowEnd
    }

    fun setStartTime(year: Long, month: Long, day: Long) {
        startingTime = getTimeStamp(year, month, day)
    }

    /**
     * Get time stamp of the given week and day of this term
     */
    fun getTermTime(week: Long, day: Long): Long =
        startingTime + (week * 7 + day) * 24 * 3600 * 1000

}

data class DDlInfo(
    var name: String,                  //DDL名字
    val id: Long,
    var endingTime: Long,              //DDL结束时间
    val prompt: String,                //DDL描述
    var startingTime: Long             //DDL开始工作时间
) {
    fun wrapTime(time: Long): String = if (time < 10) "0$time" else time.toString()
    fun getString(termInfo: TermInfo): String {
        var output = ""
        val hour = getHour(endingTime, termInfo)
        val min = getPastMin(endingTime, termInfo) - hour * 60
        output += wrapTime(hour)
        output += ":"
        output += wrapTime(min)
        return output
    }
}

data class CourseTemplate(              //模板，对应以周为单位的日历上的一块\
    var column: Long,                  //课程位于哪一列
    var startingTime: Long,           //课程开始于哪一行
    var endingTime: Long,             //课程结束于哪一行
    var period: Long                    //一周一次或两周一次(应该不存在三天一次的课吧，
    // 一周两次的话就建立两个template)
) {
    @JsonIgnore
    lateinit var info: CourseInfo
}

data class CourseInfo(
    var name: String,                  //课程名字
    var startingTime: Long,            //课程开始时间，直接使用时间戳
    var endingTime: Long,              //课程结束时间，直接使用时间戳，(前端输入时可以选择持续多少周，但后端不记录)
    var timeInfo: MutableList<CourseTemplate>,//课程时间，存放CourseTemplate的List
    var prompt: String,                //课程描述
    var location: String,               //课程位置
) {
    fun addCourse(courseTime: CourseTemplate) {
        courseTime.info = this
        timeInfo.add(courseTime)
    }
    fun addTemplate(template: MutableList<CourseTemplate>)
    {
        for (t in template)
            t.info=this
        timeInfo=template
    }
}
