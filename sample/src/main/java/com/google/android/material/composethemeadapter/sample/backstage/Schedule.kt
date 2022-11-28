package com.google.android.material.composethemeadapter.sample.backstage


import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*
import kotlin.math.abs
import kotlin.math.min


/**
 * @author WWzzEE2
 * @since v1
 *
 * The backstage functions of the program
 */

private class TemplateMap(val bindSchedule: Schedule) : TreeMap<Long, HashSet<CourseTemplate>>() {

    /**
     * Add a template
     * @return The conflict course when add fails
     */
    fun addTemplate(template: CourseTemplate, check: Boolean = true): Boolean {
        if (check && conflictCheck(template)) return false
        val set = this[template.startingTime] ?: HashSet()
        set.add(template)
        this[template.startingTime] = set
        return true
    }

    /**
     * Add a course
     */
    fun removeTemplate(template: CourseTemplate) {
        this[template.startingTime]?.remove(template)
    }

    /**
     *  Check if there are any courses at given time
     *  The course's time is treated as [startTime, endTime)
     */
    fun getTemplate(time: Long, week: Long): CourseTemplate? {
        for ((k, v) in this) {
            if (k > time) break
            for (t in v) {
                if (t.endingTime > time && bindSchedule.templateAvailable(
                        t,
                        week.toShort()
                    )
                ) return t
            }
        }
        return null
    }

    /**
     * Check the given template conflict in current map
     */
    fun conflictCheck(template: CourseTemplate): Boolean {
        for ((k, v) in this) {
            if (k > template.endingTime) break
            for (t in v) if (conflictCheck(t, template)) return true
        }
        return false
    }

    /**
     * Check 2 course template conflict or not
     * @return True when conflict
     */
    fun conflictCheck(t1: CourseTemplate, t2: CourseTemplate): Boolean {
        if (crossover(
                t1.startingTime.toInt(),
                t1.endingTime.toInt(),
                t2.startingTime.toInt(),
                t2.endingTime.toInt()
            ) &&
            crossover(
                t1.info.startingTime,
                t1.info.endingTime,
                t2.info.startingTime,
                t2.info.endingTime
            )
        ) {

            val laterT = if (t1.info.startingTime > t2.info.startingTime) t1 else t2
            val otherT = if (laterT == t1) t2 else t1
            val otherStart = bindSchedule.getWeek(otherT.info.startingTime)
            val endWeek = min(
                bindSchedule.getWeek(t1.info.endingTime),
                bindSchedule.getWeek(t2.info.endingTime)
            )

            for (w in bindSchedule.getWeek(laterT.info.startingTime) until endWeek step laterT.period) {
                if ((w - otherStart) % otherT.period == 0L) return true
            }
        }
        return false
    }

}

private class DDLMap(val bindSchedule: Schedule) : TreeMap<Long, HashSet<DDlInfo>>() {

    /**
     * @return False when ddl already exists
     */
    fun addDDl(ddl: DDlInfo): Boolean {
        val set = this[ddl.endingTime] ?: HashSet()
        this[ddl.endingTime] = set
        return set.add(ddl)
    }

    fun removeDDl(ddl: DDlInfo) =
        this[ddl.endingTime]?.let {
            it.remove(ddl)
            if (it.isEmpty()) remove(ddl.endingTime)
        }

    /**
     * DDl is different from course (about ddl at 24:00)
     */
    fun getDDl(fromTime: Long, toTime: Long): List<DDlInfo> =
        subMap(fromTime, false, toTime, true).values.let {
            val res = ArrayList<DDlInfo>()
            for (set in it)
                res.addAll(set)
            return res
        }

    fun addAll(ddlCollection: Collection<DDlInfo>) {
        for (ddl in ddlCollection)
            addDDl(ddl)
    }

    fun toList(): List<DDlInfo> = this.values.let {
        val res = ArrayList<DDlInfo>()
        for (set in it)
            res.addAll(set)
        return res
    }
}

/**
 * Config used to generate data
 */
data class TestDataConfig(
    val courseTryCnt: Int,
    val ddlCnt: Int,
    val totWeek: Long,
    val maxTime: Long
)

class Schedule(private val context: Context, testData: TestDataConfig? = null) {

    private val courseMap = Array(7) { TemplateMap(this) }

    private val ddlMap = DDLMap(this)

    private val courseSet = HashSet<CourseInfo>()

    private val scheduleContext=context
    var termInfo = TermInfo(getTimeStamp(2022, 9, 5), getTimeStamp(2023, 2, 10))
        internal set

    init {

        if (testData != null) {
            generateTestData(testData)
//            val weekSec = 1000L * 3600 * 24 * 7
//            val course = CourseInfo("Test Course", termInfo.startingTime, termInfo.startingTime + weekSec * testData.totWeek, ArrayList(), "test", "Void")
//            course.timeInfo.add(CourseTemplate(0, 0 , 2, 1))
//            course.timeInfo.add(CourseTemplate(1, 0, 1, 1))
//            for (template in course.timeInfo)
//                template.info = course
//            addCourse(course)
            saveAll()
        } else {
            load(this, context)
        }
    }

    /**
     * Generate test Data
     */
    fun generateTestData(testData: TestDataConfig) {
        val rand = Random()
        val weekSec = 1000 * 3600 * 24 * 7
        for (i in 0 until testData.ddlCnt)
            addDDl(
                DDlInfo(
                    "Test ddl$i",
                    i.toLong(),
                    abs(rand.nextLong()) % (weekSec * testData.totWeek) + termInfo.startingTime,
                    "This is DDL $i balabalawalawala\n" +
                            "bababababababa",
                    termInfo.startingTime
                )
            )
        for (i in 0 until testData.courseTryCnt) {
            val course = CourseInfo(
                "Test Course$i",
                termInfo.startingTime,
                weekSec * testData.totWeek + termInfo.startingTime,
                ArrayList(),
                "This is Course $i",
                "Classroom $i"
            )
            val strTime = (rand.nextInt(testData.maxTime.toInt())).toLong()
            val template = CourseTemplate(
                rand.nextInt(7).toLong(),
                strTime,
                min((strTime + 2 + rand.nextInt(2)), (testData.maxTime)),
                1
            )
            template.info = course
            course.timeInfo.add(template)
            addCourse(course)
        }
    }

    /**
     * @see com.example.myapplication.backstage.getWeek
     */
    fun getWeek(time: Long) = getWeek(termInfo.startingTime, time)

    /**
     * Save all data to disk
     * @see save
     */
    fun saveAll() = save(this, context)

    fun getTemplate(time: Long): CourseTemplate? =
        getTemplate(getDay(time), getHour(time, termInfo), getWeek(time))

    /**
     * @see TemplateMap.getTemplate
     */
    fun getTemplate(time: Long, colum: Long, week: Long): CourseTemplate? {
        return courseMap[colum.toInt()].getTemplate(time, week)
    }

    /**
     * Get all template in given day of week
     */
    fun getTemplate(colum: Long, week: Long): List<CourseTemplate> {
        val dayMap = courseMap[colum.toInt()]
        val res = ArrayList<CourseTemplate>()
        for ((k, v) in dayMap) {
            if (res.isNotEmpty() && k < res.last().endingTime) continue
            for (template in v) {
                if (templateAvailable(template, week.toShort())) {
                    res.add(template)
                    break
                }
            }
        }
        return res
    }

    /**
     * @see TemplateMap.addTemplate
     */
    fun addTemplate(courseTemplate: CourseTemplate): Boolean {
        return courseMap[courseTemplate.column.toInt()].addTemplate(courseTemplate)
    }

    /**
     * @see TemplateMap.removeTemplate
     */
    fun removeTemplate(courseTemplate: CourseTemplate) {
        courseMap[courseTemplate.column.toInt()].removeTemplate(courseTemplate)
    }

    /**
     * Add a course to schedule
     * @return False when add fail due to conflict
     */
    fun addCourse(course: CourseInfo): Boolean {

        for (template in course.timeInfo)
            if (courseMap[template.column.toInt()].conflictCheck(template))
                return false

        for (template in course.timeInfo)
            courseMap[template.column.toInt()].addTemplate(template, false)

        courseSet.add(course)

        return true
    }

    /**
     * Remove a course from schedule
     */
    fun removeCourse(course: CourseInfo) {
        for (template in course.timeInfo)
            courseMap[template.column.toInt()].removeTemplate(template)

        courseSet.remove(course)
    }

    /**
     * @see DDLMap.addDDl
     */
    fun addDDl(ddl: DDlInfo) {
        ddlMap.addDDl(ddl)
    }

    /**
     * @see DDLMap.removeDDl
     */
    fun removeDDl(ddl: DDlInfo) {
        ddlMap.removeDDl(ddl)
    }

    /**
     * @see DDLMap.getDDl
     */
    fun getDDlStamp(fromTime: Long, toTime: Long): List<DDlInfo> = ddlMap.getDDl(fromTime, toTime)

    /**
     * Get all ddl in given day (day and week start from 0)
     */
    fun getDDlFromRelativeTime(week: Long, day: Long): List<DDlInfo> =
        (termInfo.startingTime + 1L * (week * 7 + day) * 24 * 3600 * 1000).let {
            return getDDlStamp(it, it + 24 * 3600 * 1000)
        }

    fun getAllDDl(): List<DDlInfo> = ddlMap.toList()

    fun getAllCourse(): List<CourseInfo> = courseSet.toList()


    internal fun templateAvailable(template: CourseTemplate, week: Short): Boolean {
        val startTime = getWeek(template.info.startingTime)
        val endTime = getWeek(template.info.endingTime)
        return week in startTime until endTime && (week - startTime) % template.period == 0L
    }

    fun clear() {
        for (templateMap in courseMap)
            templateMap.clear()

        ddlMap.clear()

        courseSet.clear()

        termInfo = TermInfo(getTimeStamp(2022, 9, 5), getTimeStamp(2023, 2, 10))
    }

    fun updateWidget(){
        val mWidgetIntent = Intent()
        mWidgetIntent.action = "com.ideal.note.widget"
        scheduleContext.sendBroadcast(mWidgetIntent)
    }
}